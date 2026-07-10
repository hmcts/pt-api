package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.pt.ccd.api.CcdApiClient;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicationType;
import uk.gov.hmcts.reform.pt.ccd.event.EventId;
import uk.gov.hmcts.reform.pt.dto.CaseDto;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.exception.InvalidCaseReferenceException;
import uk.gov.hmcts.reform.pt.dto.CreateApplicationRequestDto;
import uk.gov.hmcts.reform.pt.dto.CreateApplicationResponseDto;
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

    @Mock
    private CcdApiClient ccdApiClient;

    @Captor
    private ArgumentCaptor<PTCaseEntity> ptCaseEntityCaptor;

    @InjectMocks
    private PTCaseService ptCaseService;

    @Test
    @DisplayName("Should save a case entity built from the case reference and PTCase")
    void createCase() {
        long caseReference = 1234567890123456L;
        PTCase ptCase = PTCase.builder()
            .firstName("John")
            .build();
        UUID userId = UUID.randomUUID();

        ptCaseService.createCase(caseReference, userId, ptCase);

        verify(ptCaseRepository).save(ptCaseEntityCaptor.capture());
        PTCaseEntity savedEntity = ptCaseEntityCaptor.getValue();

        assertThat(savedEntity.getCaseReference()).isEqualTo(caseReference);
        assertThat(savedEntity.getApplicantFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should start and submit a CCD event then save a case entity when creating an application")
    void createCaseFromRequest() {
        CreateApplicationRequestDto request = CreateApplicationRequestDto.builder()
            .applicantFirstName("John")
            .applicantLastName("Smith")
            .email("john.smith@example.com")
            .postcode("SW1A 1AA")
            .applicationType(ApplicationType.CHALLENGE_RENT_INCREASE)
            .build();
        UUID userId = UUID.randomUUID();
        String eventToken = "event-token";

        StartEventResponse startEventResponse = StartEventResponse.builder()
            .token(eventToken)
            .build();
        CaseDetails caseDetails = CaseDetails.builder()
            .id(CASE_REFERENCE)
            .build();

        when(ccdApiClient.startEvent(EventId.createNewApplication)).thenReturn(startEventResponse);
        when(ccdApiClient.submitCaseCreation(any(PTCase.class), eq(EventId.createNewApplication), eq(eventToken)))
            .thenReturn(caseDetails);

        CreateApplicationResponseDto response = ptCaseService.createCase(request, userId);

        assertThat(response.getCaseReference()).isEqualTo(CASE_REFERENCE);

        verify(ccdApiClient).startEvent(EventId.createNewApplication);
        verify(ccdApiClient).submitCaseCreation(any(PTCase.class), eq(EventId.createNewApplication), eq(eventToken));
        verify(ptCaseRepository).save(ptCaseEntityCaptor.capture());
        verifyNoMoreInteractions(ccdApiClient, ptCaseRepository);

        PTCaseEntity savedEntity = ptCaseEntityCaptor.getValue();
        assertThat(savedEntity.getCaseReference()).isEqualTo(CASE_REFERENCE);
        assertThat(savedEntity.getApplicantIdamUserId()).isEqualTo(userId);
        assertThat(savedEntity.getApplicantFirstName()).isEqualTo("John");
        assertThat(savedEntity.getApplicantLastName()).isEqualTo("Smith");
        assertThat(savedEntity.getEmail()).isEqualTo("john.smith@example.com");
        assertThat(savedEntity.getPostcode()).isEqualTo("SW1A 1AA");
        assertThat(savedEntity.getApplicationType()).isEqualTo(ApplicationType.CHALLENGE_RENT_INCREASE);
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
        assertThat(result.getFirst().getCaseReference()).isEqualTo(dto.getCaseReference());

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

        assertThat(result.getCaseReference()).isEqualTo(dto.getCaseReference());

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
            .caseReference(entity.getCaseReference())
            .build();
    }
}
