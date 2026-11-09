package uk.gov.hmcts.reform.pt.feesandpay.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import uk.gov.hmcts.reform.pt.feesandpay.model.FeeDetails;
import uk.gov.hmcts.reform.pt.security.IdamTokenProvider;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    private static final String AUTH_TOKEN = "Bearer system-token";
    private static final String CALLBACK_URL = "http://pt-api/payment-update";
    private static final String HMCTS_ORG_ID = "ABC1";
    private static final long CASE_REFERENCE = 1234567890123456L;

    @Mock
    private PaymentsClient paymentsClient;

    @Mock
    private IdamTokenProvider systemUpdateUserTokenProvider;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(paymentsClient, systemUpdateUserTokenProvider, CALLBACK_URL, HMCTS_ORG_ID);
        when(systemUpdateUserTokenProvider.getAuthToken()).thenReturn(AUTH_TOKEN);
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
    void shouldCreateCardPaymentAgainstServiceRequest() {
        CardPaymentServiceRequestResponse response = CardPaymentServiceRequestResponse.builder()
            .paymentReference("RC-1234-5678-9012-3456")
            .status("Initiated")
            .nextUrl("https://card.payments.service.gov.uk/secure/abc")
            .build();
        when(paymentsClient.createGovPayCardPaymentRequest(eq("2026-1234567890"), eq(AUTH_TOKEN),
                                                           any(CardPaymentServiceRequestDTO.class)))
            .thenReturn(response);

        CardPaymentServiceRequestResponse result = paymentService.createCardPayment(
            "2026-1234567890", new BigDecimal("100.00"), "English", "https://pt/payment/return");

        assertThat(result).isSameAs(response);

        ArgumentCaptor<CardPaymentServiceRequestDTO> captor =
            ArgumentCaptor.forClass(CardPaymentServiceRequestDTO.class);
        verify(paymentsClient).createGovPayCardPaymentRequest(eq("2026-1234567890"), eq(AUTH_TOKEN),
                                                              captor.capture());
        CardPaymentServiceRequestDTO request = captor.getValue();

        assertThat(request.getAmount()).isEqualByComparingTo("100.00");
        assertThat(request.getLanguage()).isEqualTo("English");
        assertThat(request.getReturnUrl()).isEqualTo("https://pt/payment/return");
        assertThat(request.getCurrency()).isEqualTo("GBP");
    }

    @Test
    void shouldGetCardPaymentStatus() {
        PaymentDto payment = PaymentDto.builder().status("Success").build();
        when(paymentsClient.getGovPayCardPaymentStatusWithCallback("RC-1234", AUTH_TOKEN)).thenReturn(payment);

        PaymentDto result = paymentService.getCardPaymentStatus("RC-1234");

        assertThat(result).isSameAs(payment);
    }
}
