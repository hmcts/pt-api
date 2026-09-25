package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicationType;
import uk.gov.hmcts.reform.pt.dto.ApplicationDto;
import uk.gov.hmcts.reform.pt.dto.EnrichedApplicationDto;
import uk.gov.hmcts.reform.pt.entity.AddressEntity;
import uk.gov.hmcts.reform.pt.entity.CaseApplicationEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyAccessEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.CaseTypeEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.TenancyDetailsEntity;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.exception.InvalidCaseReferenceException;
import uk.gov.hmcts.reform.pt.mapper.ApplicationSummaryMapper;
import uk.gov.hmcts.reform.pt.mapper.ApplicationSummaryMapperImpl;
import uk.gov.hmcts.reform.pt.repository.CaseApplicationRepository;
import uk.gov.hmcts.reform.pt.entity.projection.ApplicationSummary;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.pt.ccd.domain.TenancyType.ASSURED_PERIODIC_TENANCY;

@ExtendWith(MockitoExtension.class)
class CaseApplicationServiceTest {

    private static final long CASE_REFERENCE = 1234567890123456L;

    @Mock
    private CaseApplicationRepository applicationRepository;

    @Spy
    private ApplicationSummaryMapper applicationSummaryMapper = new ApplicationSummaryMapperImpl();

    @InjectMocks
    private CaseApplicationService applicationService;

    /**
     * Stand-in for the projection Spring Data proxies from the native query result.
     */
    private record Summary(
        Long id,
        long caseReference,
        LocalDateTime createdDate,
        LocalDateTime submittedDate
    ) implements ApplicationSummary {
        @Override
        public Long getId() {
            return id;
        }

        @Override
        public long getCaseReference() {
            return caseReference;
        }

        @Override
        public LocalDateTime getCreatedDate() {
            return createdDate;
        }

        @Override
        public LocalDateTime getSubmittedDate() {
            return submittedDate;
        }
    }

    @Test
    @DisplayName("Should get applications for a user")
    void getApplicationsForUser() {
        UUID userId = UUID.randomUUID();
        LocalDateTime created = LocalDateTime.of(2026, 1, 2, 3, 4);
        LocalDateTime submitted = LocalDateTime.of(2026, 1, 5, 6, 7);

        when(applicationRepository.findActiveByCasePartyAccessIdamId(userId))
            .thenReturn(List.of(new Summary(1L, CASE_REFERENCE, created, submitted)));

        List<ApplicationDto> result = applicationService.getCasesForUser(userId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCaseReference()).isEqualTo(CASE_REFERENCE);
        assertThat(result.getFirst().getCreatedDate()).isEqualTo(created);
        assertThat(result.getFirst().getSubmittedOn()).isEqualTo(submitted);

        verify(applicationRepository).findActiveByCasePartyAccessIdamId(userId);
        verifyNoMoreInteractions(applicationRepository);
    }

    @Test
    @DisplayName("Should get application by case reference")
    void getApplicationByCaseReference() {
        UUID userId = UUID.randomUUID();
        CaseApplicationEntity entity = createCaseApplication(CASE_REFERENCE, userId);

        when(applicationRepository.findByPartyIdamIdAndCaseReference(CASE_REFERENCE, userId))
            .thenReturn(Optional.of(entity));

        EnrichedApplicationDto result = applicationService.getCaseByCaseReference(CASE_REFERENCE, userId);

        assertThat(result.getCaseReference()).isEqualTo(CASE_REFERENCE);
        assertThat(result.getTenancyType()).isEqualTo(ASSURED_PERIODIC_TENANCY);

        verify(applicationRepository).findByPartyIdamIdAndCaseReference(CASE_REFERENCE, userId);
        verifyNoMoreInteractions(applicationRepository);
    }

    @Test
    @DisplayName("Should throw InvalidCaseReferenceException when case reference is zero")
    void getApplicationByCaseReferenceInvalidCaseReference() {
        assertThatThrownBy(() -> applicationService.getCaseByCaseReference(0L, UUID.randomUUID()))
            .isInstanceOf(InvalidCaseReferenceException.class)
            .hasMessage("Invalid case reference: 0");

        verifyNoMoreInteractions(applicationRepository);
    }

    @Test
    @DisplayName("Should throw CaseNotFoundException when case reference does not exist")
    void getApplicationByCaseReferenceCaseNotFound() {
        UUID userId = UUID.randomUUID();
        when(applicationRepository.findByPartyIdamIdAndCaseReference(eq(CASE_REFERENCE), any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.getCaseByCaseReference(CASE_REFERENCE, userId))
            .isInstanceOf(CaseNotFoundException.class)
            .hasMessage("No case found with reference " + CASE_REFERENCE);

        verify(applicationRepository).findByPartyIdamIdAndCaseReference(eq(CASE_REFERENCE), any());
        verifyNoMoreInteractions(applicationRepository);
    }

    private CaseApplicationEntity createCaseApplication(long caseReference, UUID userId) {
        PTCaseEntity ptCase = PTCaseEntity.builder()
            .caseReference(caseReference)
            .addresses(List.of(AddressEntity.builder().postcode("AB12 3CD").build()))
            .tenancyDetails(List.of(TenancyDetailsEntity.builder().tenancyType(ASSURED_PERIODIC_TENANCY).build()))
            .build();

        CasePartyEntity caseParty = CasePartyEntity.builder()
            .firstName("FirstName")
            .lastName("LastName")
            .emailAddress("test@test.com")
            .ptCase(ptCase)
            .build();

        CasePartyAccessEntity access = CasePartyAccessEntity.builder()
            .idamId(userId)
            .party(caseParty)
            .build();

        caseParty.setAccess(List.of(access));

        return CaseApplicationEntity.builder()
            .caseParty(caseParty)
            .caseType(CaseTypeEntity.builder()
                          .applicationTypeName(ApplicationType.CHALLENGE_EXCESSIVE_RENT).build())
            .build();
    }
}
