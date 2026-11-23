package uk.gov.hmcts.reform.pt.feesandpay.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.payments.client.PaymentsClient;
import uk.gov.hmcts.reform.payments.client.models.FeeDto;
import uk.gov.hmcts.reform.payments.client.models.PaymentDto;
import uk.gov.hmcts.reform.payments.request.CardPaymentServiceRequestDTO;
import uk.gov.hmcts.reform.payments.request.CreateServiceRequestDTO;
import uk.gov.hmcts.reform.payments.response.CardPaymentServiceRequestResponse;
import uk.gov.hmcts.reform.payments.response.PaymentServiceResponse;
import uk.gov.hmcts.reform.pt.entity.ApplicationFeeEntity;
import uk.gov.hmcts.reform.pt.feesandpay.exception.FeeAlreadyPaidException;
import uk.gov.hmcts.reform.pt.feesandpay.exception.FeePaymentNotFoundException;
import uk.gov.hmcts.reform.pt.feesandpay.model.CardPaymentStatusResponse;
import uk.gov.hmcts.reform.pt.feesandpay.model.CreateCardPaymentRequest;
import uk.gov.hmcts.reform.pt.feesandpay.model.CreateCardPaymentResponse;
import uk.gov.hmcts.reform.pt.feesandpay.model.FeeDetails;
import uk.gov.hmcts.reform.pt.feesandpay.model.Payment;
import uk.gov.hmcts.reform.pt.feesandpay.model.PaymentStatusCallback;
import uk.gov.hmcts.reform.pt.repository.ApplicationFeeRepository;
import uk.gov.hmcts.reform.pt.security.IdamTokenProvider;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    private static final String AUTH_TOKEN = "Bearer system-token";
    private static final String CALLBACK_URL = "http://pt-api/payment-update";
    private static final String HMCTS_ORG_ID = "ABC1";
    private static final long CASE_REFERENCE = 1234567890123456L;
    private static final String SERVICE_REQUEST_REFERENCE = "2026-1234567890";

    @Mock
    private PaymentsClient paymentsClient;

    @Mock
    private IdamTokenProvider systemUpdateUserTokenProvider;

    @Mock
    private ApplicationFeeRepository applicationFeeRepository;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(
            paymentsClient,
            systemUpdateUserTokenProvider,
            applicationFeeRepository,
            CALLBACK_URL,
            HMCTS_ORG_ID
        );
    }

    @Test
    void shouldCreateServiceRequestAndReturnReference() {
        FeeDetails fee = FeeDetails.builder()
            .code("FEE0001")
            .version(3)
            .feeAmount(new BigDecimal("100.00"))
            .build();
        when(paymentsClient.createServiceRequest(eq(AUTH_TOKEN), any(CreateServiceRequestDTO.class)))
            .thenReturn(PaymentServiceResponse.builder().serviceRequestReference("2026-1234567890").build());
        when(systemUpdateUserTokenProvider.getAuthToken()).thenReturn(AUTH_TOKEN);

        String result = paymentService.createServiceRequest(CASE_REFERENCE, fee, "Jane Smith");

        assertThat(result).isEqualTo("2026-1234567890");

        ArgumentCaptor<CreateServiceRequestDTO> captor = ArgumentCaptor.forClass(CreateServiceRequestDTO.class);
        verify(paymentsClient).createServiceRequest(eq(AUTH_TOKEN), captor.capture());
        CreateServiceRequestDTO request = captor.getValue();

        assertThat(request.getCallBackUrl()).isEqualTo(CALLBACK_URL);
        assertThat(request.getHmctsOrgId()).isEqualTo(HMCTS_ORG_ID);
        assertThat(request.getCcdCaseNumber()).isEqualTo(String.valueOf(CASE_REFERENCE));
        assertThat(request.getCaseReference()).isEqualTo(String.valueOf(CASE_REFERENCE));
        assertThat(request.getCasePaymentRequest().getAction()).isEqualTo("payment");
        assertThat(request.getCasePaymentRequest().getResponsibleParty()).isEqualTo("Jane Smith");
        assertThat(request.getFees()).hasSize(1);

        FeeDto feeDto = request.getFees()[0];
        assertThat(feeDto.getCode()).isEqualTo("FEE0001");
        assertThat(feeDto.getVersion()).isEqualTo("3");
        assertThat(feeDto.getCalculatedAmount()).isEqualByComparingTo("100.00");
        assertThat(feeDto.getVolume()).isEqualTo(1);
    }

    @Test
    void shouldCreateCardPaymentRequestForUnpaidApplicationFee() {
        when(applicationFeeRepository.findByRequestReference(SERVICE_REQUEST_REFERENCE))
            .thenReturn(Optional.of(applicationFeeWithStatus("Not paid")));
        when(systemUpdateUserTokenProvider.getAuthToken()).thenReturn(AUTH_TOKEN);
        when(paymentsClient.createGovPayCardPaymentRequest(eq(SERVICE_REQUEST_REFERENCE), eq(AUTH_TOKEN),
                                                           any(CardPaymentServiceRequestDTO.class)))
            .thenReturn(CardPaymentServiceRequestResponse.builder()
                            .paymentReference("RC-1234-5678-9012-3456")
                            .status("Initiated")
                            .nextUrl("https://card.payments.service.gov.uk/secure/abc")
                            .build());

        CreateCardPaymentResponse result = paymentService.createCardPaymentRequest(
            SERVICE_REQUEST_REFERENCE, cardPaymentRequest());

        assertThat(result.getPaymentReference()).isEqualTo("RC-1234-5678-9012-3456");
        assertThat(result.getStatus()).isEqualTo("Initiated");
        assertThat(result.getNextUrl()).isEqualTo("https://card.payments.service.gov.uk/secure/abc");

        ArgumentCaptor<CardPaymentServiceRequestDTO> captor =
            ArgumentCaptor.forClass(CardPaymentServiceRequestDTO.class);
        verify(paymentsClient).createGovPayCardPaymentRequest(eq(SERVICE_REQUEST_REFERENCE), eq(AUTH_TOKEN),
                                                              captor.capture());
        CardPaymentServiceRequestDTO request = captor.getValue();
        assertThat(request.getAmount()).isEqualByComparingTo("47.00");
        assertThat(request.getLanguage()).isEqualTo("English");
        assertThat(request.getReturnUrl()).isEqualTo("https://pt/payment/return");
    }

    @Test
    void shouldThrowWhenNoApplicationFeeForServiceRequest() {
        when(applicationFeeRepository.findByRequestReference(SERVICE_REQUEST_REFERENCE))
            .thenReturn(Optional.empty());

        CreateCardPaymentRequest request = cardPaymentRequest();
        assertThatThrownBy(() -> paymentService.createCardPaymentRequest(SERVICE_REQUEST_REFERENCE, request))
            .isInstanceOf(FeePaymentNotFoundException.class);

        verifyNoInteractions(paymentsClient);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Paid", "Partially paid"})
    void shouldThrowWhenApplicationFeeAlreadyPaid(String status) {
        when(applicationFeeRepository.findByRequestReference(SERVICE_REQUEST_REFERENCE))
            .thenReturn(Optional.of(applicationFeeWithStatus(status)));

        CreateCardPaymentRequest request = cardPaymentRequest();
        assertThatThrownBy(() -> paymentService.createCardPaymentRequest(SERVICE_REQUEST_REFERENCE, request))
            .isInstanceOf(FeeAlreadyPaidException.class);

        verifyNoInteractions(paymentsClient);
    }

    @Test
    void shouldGetCardPaymentStatus() {
        PaymentDto paymentDto = PaymentDto.builder().status("Success").build();
        when(paymentsClient.getGovPayCardPaymentStatusWithCallback("RC-1234", AUTH_TOKEN))
            .thenReturn(paymentDto);
        when(systemUpdateUserTokenProvider.getAuthToken()).thenReturn(AUTH_TOKEN);

        CardPaymentStatusResponse result = paymentService.getCardPaymentStatus("RC-1234");

        assertThat(result.getStatus()).isEqualTo("Success");
    }

    private static ApplicationFeeEntity applicationFeeWithStatus(String status) {
        return ApplicationFeeEntity.builder()
            .requestReference(SERVICE_REQUEST_REFERENCE)
            .status(status)
            .amount(new BigDecimal("47.00"))
            .build();
    }

    private static CreateCardPaymentRequest cardPaymentRequest() {
        return CreateCardPaymentRequest.builder()
            .language("English")
            .returnUrl("https://pt/payment/return")
            .build();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Paid", "Partially paid", "Not paid"})
    void shouldUpdateStatusFromCallback(String status) {
        String serviceRequestRef = "2025-1712345678901";
        ApplicationFeeEntity fee = ApplicationFeeEntity.builder()
            .requestReference(serviceRequestRef)
            .build();
        when(applicationFeeRepository.findByRequestReference(serviceRequestRef)).thenReturn(Optional.of(fee));

        String paymentRef = "RC-1712-3456-7890-1234";
        PaymentStatusCallback callback = PaymentStatusCallback.builder()
            .serviceRequestReference(serviceRequestRef)
            .serviceRequestStatus(status)
            .payment(Payment.builder().paymentReference(paymentRef).build())
            .build();

        paymentService.processPaymentResponse(callback);

        verify(applicationFeeRepository).save(fee);
        assertThat(fee.getStatus()).isEqualTo(status);
        assertThat(fee.getExternalReference()).isEqualTo(paymentRef);
    }

    @Test
    void shouldNotSaveWhenApplicationFeeNotFound() {
        PaymentStatusCallback callback = PaymentStatusCallback.builder()
            .serviceRequestReference("2025-1712345678901")
            .ccdCaseNumber("1712345678901234")
            .serviceRequestAmount(new BigDecimal("47.00"))
            .serviceRequestStatus("Paid")
            .payment(Payment.builder().paymentReference("RC-1712-3456-7890-1234").build())
            .build();
        when(applicationFeeRepository.findByRequestReference("2025-1712345678901")).thenReturn(Optional.empty());

        paymentService.processPaymentResponse(callback);

        verify(applicationFeeRepository, never()).save(any());
    }
}
