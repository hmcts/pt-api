package uk.gov.hmcts.reform.pt.feesandpay.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.support.TransactionTemplate;
import uk.gov.hmcts.reform.pt.config.AbstractPostgresContainerIT;
import uk.gov.hmcts.reform.pt.entity.ApplicationFeeEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.feesandpay.model.FeeDetails;
import uk.gov.hmcts.reform.pt.repository.ApplicationFeeRepository;
import uk.gov.hmcts.reform.pt.repository.PTCaseRepository;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.pt.ccd.domain.PartyRole.APPLICANT;
import static uk.gov.hmcts.reform.pt.feesandpay.model.FeeType.APPLICATION_FEE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ApplicationFeeServiceIT extends AbstractPostgresContainerIT {

    @Autowired
    private ApplicationFeeService applicationFeeService;
    @Autowired
    private PTCaseRepository ptCaseRepository;
    @Autowired
    private ApplicationFeeRepository applicationFeeRepository;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @MockitoBean
    private FeeService feeService;
    @MockitoBean
    private PaymentService paymentService;

    private long caseReference;
    private String serviceRequestReference;

    @BeforeEach
    void setUp() {
        caseReference = 1_000_000_000_000_000L + ThreadLocalRandom.current().nextLong(999_999_999_999_999L);
        PTCaseEntity ptCase = PTCaseEntity.builder().caseReference(caseReference).build();
        CasePartyEntity applicant = CasePartyEntity.builder()
            .firstName("Jane")
            .lastName("Smith")
            .casePartyRole(APPLICANT)
            .ptCase(ptCase)
            .build();
        ptCase.getParties().add(applicant);
        ptCaseRepository.save(ptCase);

        serviceRequestReference = "2026-" + caseReference;
        FeeDetails fee = FeeDetails.builder()
            .code("FEE0569")
            .version(1)
            .feeAmount(new BigDecimal("47.00"))
            .build();
        when(feeService.getFee(APPLICATION_FEE)).thenReturn(fee);
        when(paymentService.createServiceRequest(caseReference, fee, "Jane Smith"))
            .thenReturn(serviceRequestReference);
    }

    @Test
    void shouldSaveApplicationFeeLinkedToMarketRentCase() {
        applicationFeeService.requestApplicationFee(caseReference);

        ApplicationFeeEntity saved = applicationFeeRepository.findByRequestReference(serviceRequestReference)
            .orElseThrow();
        assertThat(saved.getAmount()).isEqualByComparingTo("47.00");
        assertThat(saved.getStatus()).isEqualTo("Not paid");
        assertThat(saved.getRequestDate()).isNotNull();

        Long linkedCaseReference = transactionTemplate.execute(status -> applicationFeeRepository
            .findByRequestReference(serviceRequestReference)
            .orElseThrow()
            .getMarketRentCase()
            .getPtCase()
            .getCaseReference());
        assertThat(linkedCaseReference).isEqualTo(caseReference);
    }

    @Test
    void shouldReturnExistingApplicationFeeWhenRequestedTwice() {
        ApplicationFeeEntity first = applicationFeeService.requestApplicationFee(caseReference);
        ApplicationFeeEntity second = applicationFeeService.requestApplicationFee(caseReference);

        assertThat(second.getId()).isEqualTo(first.getId());
        verify(paymentService, times(1)).createServiceRequest(anyLong(), any(FeeDetails.class), anyString());
    }
}
