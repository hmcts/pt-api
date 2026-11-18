package uk.gov.hmcts.reform.pt.feesandpay.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.payments.client.PaymentsClient;
import uk.gov.hmcts.reform.payments.client.models.CasePaymentRequestDto;
import uk.gov.hmcts.reform.payments.client.models.FeeDto;
import uk.gov.hmcts.reform.payments.client.models.PaymentDto;
import uk.gov.hmcts.reform.payments.request.CardPaymentServiceRequestDTO;
import uk.gov.hmcts.reform.payments.request.CreateServiceRequestDTO;
import uk.gov.hmcts.reform.payments.response.CardPaymentServiceRequestResponse;
import uk.gov.hmcts.reform.payments.response.PaymentServiceResponse;
import uk.gov.hmcts.reform.pt.feesandpay.model.FeeDetails;
import uk.gov.hmcts.reform.pt.feesandpay.model.PaymentStatusCallback;
import uk.gov.hmcts.reform.pt.repository.ApplicationFeeRepository;
import uk.gov.hmcts.reform.pt.security.IdamTokenProvider;

import java.math.BigDecimal;

@Slf4j
@Service
public class PaymentService {

    private static final String ACTION_PAYMENT = "payment";

    private final PaymentsClient paymentsClient;
    private final IdamTokenProvider systemUpdateUserTokenProvider;
    private final ApplicationFeeRepository applicationFeeRepository;
    private final String callbackUrl;
    private final String hmctsOrgId;

    public PaymentService(PaymentsClient paymentsClient,
                          @Qualifier("systemUpdateUserTokenProvider") IdamTokenProvider systemUpdateUserTokenProvider,
                          ApplicationFeeRepository applicationFeeRepository,
                          @Value("${payments.api.callback-url}") String callbackUrl,
                          @Value("${payments.params.hmctsOrgId}") String hmctsOrgId) {
        this.paymentsClient = paymentsClient;
        this.systemUpdateUserTokenProvider = systemUpdateUserTokenProvider;
        this.applicationFeeRepository = applicationFeeRepository;
        this.callbackUrl = callbackUrl;
        this.hmctsOrgId = hmctsOrgId;
    }

    public String createServiceRequest(long caseReference, FeeDetails fee, String responsibleParty) {
        CreateServiceRequestDTO request = CreateServiceRequestDTO.builder()
            .callBackUrl(callbackUrl)
            .hmctsOrgId(hmctsOrgId)
            .ccdCaseNumber(String.valueOf(caseReference))
            .caseReference(String.valueOf(caseReference))
            .casePaymentRequest(CasePaymentRequestDto.builder()
                                    .action(ACTION_PAYMENT)
                                    .responsibleParty(responsibleParty)
                                    .build())
            .fees(new FeeDto[]{FeeDto.builder()
                .code(fee.getCode())
                .version(String.valueOf(fee.getVersion()))
                .calculatedAmount(fee.getFeeAmount())
                .volume(1)
                .build()})
            .build();

        PaymentServiceResponse response = paymentsClient.createServiceRequest(
            systemUpdateUserTokenProvider.getAuthToken(), request);
        log.info("Created service request {} for case {}", response.getServiceRequestReference(), caseReference);
        return response.getServiceRequestReference();
    }

    public CardPaymentServiceRequestResponse createCardPayment(String serviceRequestReference,
                                                               BigDecimal amount,
                                                               String language,
                                                               String returnUrl) {
        CardPaymentServiceRequestDTO request = CardPaymentServiceRequestDTO.builder()
            .amount(amount)
            .language(language)
            .returnUrl(returnUrl)
            .build();

        return paymentsClient.createGovPayCardPaymentRequest(
            serviceRequestReference, systemUpdateUserTokenProvider.getAuthToken(), request);
    }

    public PaymentDto getCardPaymentStatus(String internalReference) {
        return paymentsClient.getGovPayCardPaymentStatusWithCallback(
            internalReference, systemUpdateUserTokenProvider.getAuthToken());
    }

    @Transactional
    public void processPaymentResponse(PaymentStatusCallback callback) {
        String serviceRequestReference = callback.getServiceRequestReference();
        log.info("Payment callback for service request {} with status {}",
                 serviceRequestReference, callback.getServiceRequestStatus());

        applicationFeeRepository.findByRequestReference(serviceRequestReference)
            .ifPresentOrElse(
                fee -> {
                    fee.setExternalReference(callback.getPaymentReference());
                    fee.setStatus(callback.getServiceRequestStatus());
                    applicationFeeRepository.save(fee);
                },
                () -> log.error("Unable to find an application fee with service request reference: {}",
                                serviceRequestReference)
            );
    }
}
