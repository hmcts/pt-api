package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.entity.CaseParty;
import uk.gov.hmcts.reform.pt.entity.CaseType;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.CaseApplicationRepository;
import uk.gov.hmcts.reform.pt.repository.PTCaseRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PTCaseServiceTest {

    @Mock
    private PTCaseRepository ptCaseRepository;

    @Mock
    private CasePartyService casePartyService;

    @Mock
    private CaseTypeService caseTypeService;

    @Mock
    private CaseApplicationRepository caseApplicationRepository;

    @Captor
    private ArgumentCaptor<PTCaseEntity> ptCaseEntityCaptor;

    @InjectMocks
    private PTCaseService ptCaseService;

    @Test
    @DisplayName("Should save a case entity built from the case reference when party does not exist")
    void createCasePartyDoesNotExist() {
        String applicationType = "Possession";
        UUID userId = UUID.randomUUID();

        when(casePartyService.getCasePartyByIdamId(userId)).thenReturn(Optional.empty());
        when(casePartyService.createCaseParty(any(), any())).thenReturn(CaseParty.builder().build());
        when(caseTypeService.getCaseTypeOrCreateIfNotExists(applicationType)).thenReturn(CaseType.builder().build());

        PTCase ptCase = PTCase.builder()
            .applicantFirstName("John")
            .applicationType(applicationType)
            .build();
        long caseReference = 1234567890123456L;

        ptCaseService.createCase(caseReference, userId, ptCase);

        verify(ptCaseRepository).save(ptCaseEntityCaptor.capture());
        PTCaseEntity savedEntity = ptCaseEntityCaptor.getValue();

        assertThat(savedEntity.getCaseReference()).isEqualTo(caseReference);
        verify(casePartyService).createCaseParty(ptCase, userId);
    }

    @Test
    @DisplayName("Should save a case entity built from the case reference when party exists")
    void createCasePartyExists() {
        long caseReference = 1234567890123456L;
        String applicationType = "Possession";
        PTCase ptCase = PTCase.builder()
            .applicantFirstName("John")
            .applicationType(applicationType)
            .build();
        UUID userId = UUID.randomUUID();
        CaseParty existingParty = CaseParty.builder().firstName("John").build();

        when(casePartyService.getCasePartyByIdamId(userId)).thenReturn(Optional.of(existingParty));
        when(caseTypeService.getCaseTypeOrCreateIfNotExists(applicationType)).thenReturn(CaseType.builder().build());

        ptCaseService.createCase(caseReference, userId, ptCase);

        verify(ptCaseRepository).save(ptCaseEntityCaptor.capture());
        PTCaseEntity savedEntity = ptCaseEntityCaptor.getValue();

        assertThat(savedEntity.getCaseReference()).isEqualTo(caseReference);
        verify(casePartyService, never()).createCaseParty(any(), any());
    }
}
