package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pt.dto.CaseDto;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.exception.InvalidCaseReferenceException;
import uk.gov.hmcts.reform.pt.repository.PTCaseRepository;

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
class PTCaseServiceTest {

    private static final long CASE_REFERENCE = 1234567890123456L;

    @Mock
    private PTCaseRepository ptCaseRepository;

    @Captor
    private ArgumentCaptor<PTCaseEntity> ptCaseEntityCaptor;

    @InjectMocks
    private PTCaseService ptCaseService;

    @Test
    @DisplayName("Should save a case entity built from the case reference and PTCase")
    void createCase() {
        long caseReference = 1234567890123456L;
        PTCase ptCase = PTCase.builder()
            .applicantForename("John")
            .build();

        ptCaseService.createCase(caseReference, ptCase);

        verify(ptCaseRepository).save(ptCaseEntityCaptor.capture());
        PTCaseEntity savedEntity = ptCaseEntityCaptor.getValue();

        assertThat(savedEntity.getCaseReference()).isEqualTo(caseReference);
        assertThat(savedEntity.getApplicantFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should get applications for a user")
    void getApplicationsForUser() {
        PTCaseEntity entity = createPtCase();
        CaseDto dto = createApplicationDto(entity);

        when(ptCaseRepository.findAllByApplicantIdamUserId(entity.getApplicantIdamUserId()))
            .thenReturn(List.of(entity));

        List<CaseDto> result = ptCaseService.getCasesForUser(entity.getApplicantIdamUserId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(dto.getId());

        verify(ptCaseRepository).findAllByApplicantIdamUserId(entity.getApplicantIdamUserId());
        verifyNoMoreInteractions(ptCaseRepository);
    }

    @Test
    @DisplayName("Should get application by case reference")
    void getApplicationByCaseReference() {
        PTCaseEntity entity = createPtCase();
        CaseDto dto = createApplicationDto(entity);
        UUID idamUserId = entity.getApplicantIdamUserId();

        when(ptCaseRepository.findByCaseReferenceAndApplicantIdamUserId(CASE_REFERENCE, idamUserId))
            .thenReturn(Optional.of(entity));

        CaseDto result = ptCaseService.getCaseByCaseReference(CASE_REFERENCE, idamUserId);

        assertThat(result.getId()).isEqualTo(dto.getId());

        verify(ptCaseRepository).findByCaseReferenceAndApplicantIdamUserId(CASE_REFERENCE, idamUserId);
        verifyNoMoreInteractions(ptCaseRepository);
    }

    @Test
    @DisplayName("Should throw InvalidCaseReferenceException when case reference is zero")
    void getApplicationByCaseReferenceInvalidCaseReference() {
        assertThatThrownBy(() -> ptCaseService.getCaseByCaseReference(0L, UUID.randomUUID()))
            .isInstanceOf(InvalidCaseReferenceException.class)
            .hasMessage("Invalid case reference: 0");

        verifyNoMoreInteractions(ptCaseRepository);
    }

    @Test
    @DisplayName("Should throw CaseNotFoundException when case reference does not exist")
    void getApplicationByCaseReferenceCaseNotFound() {
        when(ptCaseRepository.findByCaseReferenceAndApplicantIdamUserId(eq(CASE_REFERENCE), any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> ptCaseService.getCaseByCaseReference(CASE_REFERENCE, UUID.randomUUID()))
            .isInstanceOf(CaseNotFoundException.class)
            .hasMessage("No case found with reference " + CASE_REFERENCE);

        verify(ptCaseRepository).findByCaseReferenceAndApplicantIdamUserId(eq(CASE_REFERENCE), any());
        verifyNoMoreInteractions(ptCaseRepository);
    }

    private static PTCaseEntity createPtCase() {
        return PTCaseEntity.builder()
            .id(UUID.randomUUID())
            .caseReference(CASE_REFERENCE)
            .applicantIdamUserId(UUID.randomUUID())
            .build();
    }

    private static CaseDto createApplicationDto(PTCaseEntity entity) {
        return CaseDto.builder()
            .id(entity.getId())
            .caseReference(entity.getCaseReference())
            .build();
    }
}
