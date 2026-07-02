package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.PTCaseRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PTCaseServiceTest {

    @Mock
    private PTCaseRepository ptCaseRepository;

    @Captor
    private ArgumentCaptor<PTCaseEntity> ptCaseEntityCaptor;

    private PTCaseService ptCaseService;

    @Test
    @DisplayName("Should save a case entity built from the case reference and PTCase")
    void createCase() {
        ptCaseService = new PTCaseService(ptCaseRepository);

        long caseReference = 1234567890123456L;
        PTCase ptCase = PTCase.builder()
            .applicantForename("John")
            .build();

        ptCaseService.createCase(caseReference, ptCase);

        verify(ptCaseRepository).save(ptCaseEntityCaptor.capture());
        PTCaseEntity savedEntity = ptCaseEntityCaptor.getValue();

        assertThat(savedEntity.getCaseReference()).isEqualTo(caseReference);
        assertThat(savedEntity.getApplicantForename()).isEqualTo("John");
    }
}
