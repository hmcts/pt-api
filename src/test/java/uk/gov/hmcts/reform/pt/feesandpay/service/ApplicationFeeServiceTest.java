package uk.gov.hmcts.reform.pt.feesandpay.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pt.entity.ApplicationFeeEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.feesandpay.exception.FeeNotFoundException;
import uk.gov.hmcts.reform.pt.feesandpay.model.FeeDetails;
import uk.gov.hmcts.reform.pt.repository.ApplicationFeeRepository;
import uk.gov.hmcts.reform.pt.repository.PTCaseRepository;
import uk.gov.hmcts.reform.pt.service.CasePartyService;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.pt.ccd.domain.PartyRole.APPLICANT;
import static uk.gov.hmcts.reform.pt.feesandpay.model.FeeType.APPLICATION_FEE;

@ExtendWith(MockitoExtension.class)
class ApplicationFeeServiceTest {

    private static final long CASE_REFERENCE = 1234567890123456L;
    private static final String SERVICE_REQUEST_REFERENCE = "2026-1234567890";
    private static final Instant NOW = Instant.parse("2026-11-19T10:15:30Z");

    @Mock
    private CasePartyService casePartyService;
    @Mock
    private FeeService feeService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private PTCaseRepository ptCaseRepository;
    @Mock
    private ApplicationFeeRepository applicationFeeRepository;

    private ApplicationFeeService applicationFeeService;

    private PTCaseEntity ptCase;
    private final FeeDetails fee = FeeDetails.builder()
        .code("FEE0569")
        .version(1)
        .feeAmount(new BigDecimal("47.00"))
        .build();

    @BeforeEach
    void setUp() {
        applicationFeeService = new ApplicationFeeService(
            casePartyService,
            feeService,
            paymentService,
            ptCaseRepository,
            applicationFeeRepository,
            Clock.fixed(NOW, ZoneOffset.UTC)
        );
        ptCase = PTCaseEntity.builder().caseReference(CASE_REFERENCE).build();
    }

    @Test
    void shouldCreateServiceRequestAndSaveApplicationFee() {
        givenCaseWithApplicant();
        when(feeService.getFee(APPLICATION_FEE)).thenReturn(fee);
        when(paymentService.createServiceRequest(CASE_REFERENCE, fee, "Jane Smith"))
            .thenReturn(SERVICE_REQUEST_REFERENCE);
        when(applicationFeeRepository.save(any(ApplicationFeeEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        ApplicationFeeEntity result = applicationFeeService.requestApplicationFee(CASE_REFERENCE);

        ArgumentCaptor<ApplicationFeeEntity> captor = ArgumentCaptor.forClass(ApplicationFeeEntity.class);
        verify(applicationFeeRepository).save(captor.capture());
        ApplicationFeeEntity saved = captor.getValue();

        assertThat(result).isSameAs(saved);
        assertThat(saved.getRequestReference()).isEqualTo(SERVICE_REQUEST_REFERENCE);
        assertThat(saved.getRequestDate()).isEqualTo(LocalDateTime.ofInstant(NOW, ZoneOffset.UTC));
        assertThat(saved.getAmount()).isEqualByComparingTo("47.00");
        assertThat(saved.getStatus()).isEqualTo("Not paid");
    }

    @Test
    void shouldReturnExistingApplicationFeeWithoutCreatingAnotherServiceRequest() {
        ApplicationFeeEntity existing = ApplicationFeeEntity.builder()
            .requestReference(SERVICE_REQUEST_REFERENCE)
            .build();
        ptCase.setApplicationFee(existing);
        when(ptCaseRepository.findByCaseReference(CASE_REFERENCE)).thenReturn(Optional.of(ptCase));

        ApplicationFeeEntity result = applicationFeeService.requestApplicationFee(CASE_REFERENCE);

        assertThat(result).isSameAs(existing);
        verifyNoInteractions(feeService, paymentService, casePartyService);
        verify(applicationFeeRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCaseNotFound() {
        when(ptCaseRepository.findByCaseReference(CASE_REFERENCE)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationFeeService.requestApplicationFee(CASE_REFERENCE))
            .isInstanceOf(CaseNotFoundException.class);

        verifyNoInteractions(feeService, paymentService, applicationFeeRepository);
    }

    @Test
    void shouldThrowWhenCaseHasNoApplicant() {
        when(ptCaseRepository.findByCaseReference(CASE_REFERENCE)).thenReturn(Optional.of(ptCase));
        when(casePartyService.getPartyForCaseByRole(ptCase, APPLICANT)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationFeeService.requestApplicationFee(CASE_REFERENCE))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining(String.valueOf(CASE_REFERENCE));

        verifyNoInteractions(feeService, paymentService, applicationFeeRepository);
    }

    @Test
    void shouldNotCreateServiceRequestWhenFeeLookupFails() {
        givenCaseWithApplicant();
        when(feeService.getFee(APPLICATION_FEE)).thenThrow(new FeeNotFoundException("Fee not found"));

        assertThatThrownBy(() -> applicationFeeService.requestApplicationFee(CASE_REFERENCE))
            .isInstanceOf(FeeNotFoundException.class);

        verifyNoInteractions(paymentService, applicationFeeRepository);
    }

    @Test
    void shouldNotSaveApplicationFeeWhenServiceRequestFails() {
        givenCaseWithApplicant();
        when(feeService.getFee(APPLICATION_FEE)).thenReturn(fee);
        when(paymentService.createServiceRequest(anyLong(), any(FeeDetails.class), anyString()))
            .thenThrow(new RuntimeException("payment-api unavailable"));

        assertThatThrownBy(() -> applicationFeeService.requestApplicationFee(CASE_REFERENCE))
            .hasMessage("payment-api unavailable");

        verifyNoInteractions(applicationFeeRepository);
        assertThat(ptCase.getApplicationFee()).isNull();
    }

    private void givenCaseWithApplicant() {
        CasePartyEntity applicant = CasePartyEntity.builder()
            .firstName("Jane")
            .lastName("Smith")
            .casePartyRole(APPLICANT)
            .build();
        when(ptCaseRepository.findByCaseReference(CASE_REFERENCE)).thenReturn(Optional.of(ptCase));
        when(casePartyService.getPartyForCaseByRole(ptCase, APPLICANT)).thenReturn(Optional.of(applicant));
    }
}
