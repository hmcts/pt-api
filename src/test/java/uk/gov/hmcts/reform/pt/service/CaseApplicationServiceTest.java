package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pt.dto.ApplicationDto;
import uk.gov.hmcts.reform.pt.entity.CaseApplicationEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyAccessEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.CasePropertyEntity;
import uk.gov.hmcts.reform.pt.entity.CaseTypeEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.exception.InvalidCaseReferenceException;
import uk.gov.hmcts.reform.pt.repository.CaseApplicationRepository;

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

@ExtendWith(MockitoExtension.class)
class CaseApplicationServiceTest {

    private static final long CASE_REFERENCE = 1234567890123456L;

    @Mock
    private CaseApplicationRepository applicationRepository;

    @InjectMocks
    private CaseApplicationService applicationService;

    @Test
    @DisplayName("Should get applications for a user")
    void getApplicationsForUser() {
        UUID userId = UUID.randomUUID();
        CaseApplicationEntity entity = createCaseApplication(CASE_REFERENCE, userId);

        when(applicationRepository.findAllByCasePartyAccessIdamId(userId))
            .thenReturn(List.of(entity));

        List<ApplicationDto> result = applicationService.getCasesForUser(userId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCaseReference()).isEqualTo(CASE_REFERENCE);

        verify(applicationRepository).findAllByCasePartyAccessIdamId(userId);
        verifyNoMoreInteractions(applicationRepository);
    }

    @Test
    @DisplayName("Should get application by case reference")
    void getApplicationByCaseReference() {
        UUID userId = UUID.randomUUID();
        CaseApplicationEntity entity = createCaseApplication(CASE_REFERENCE, userId);

        when(applicationRepository.findByPartyIdamIdAndCaseReference(CASE_REFERENCE, userId))
            .thenReturn(Optional.of(entity));

        ApplicationDto result = applicationService.getCaseByCaseReference(CASE_REFERENCE, userId);

        assertThat(result.getCaseReference()).isEqualTo(CASE_REFERENCE);

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
            .properties(List.of(CasePropertyEntity.builder().postcode("AB12 3CD").build()))
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
            .caseType(CaseTypeEntity.builder().applicationTypeName("CHALLENGE_RENT_INCREASE").build())
            .build();
    }
}
