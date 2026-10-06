package uk.gov.hmcts.reform.pt.ccd.event.citizen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.api.callback.SubmitResponse;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.ccd.event.BaseEventTest;
import uk.gov.hmcts.reform.pt.service.PTCaseService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CitizenUploadDocumentTest extends BaseEventTest {

    @Mock
    private PTCaseService ptCaseService;

    @BeforeEach
    void setUp() {
        configureEvent(new CitizenUploadDocument(ptCaseService));
    }

    @Test
    @DisplayName("Should persist the uploaded documents against the case")
    void submitShouldPersistDocuments() {
        PTCase caseData = getTestPTCase();

        SubmitResponse<State> result = callSubmitHandler(caseData);

        verify(ptCaseService).updateDocuments(TEST_CASE_REFERENCE, caseData);
        assertThat(result.getErrors()).isEmpty();
    }

    @Test
    @DisplayName("Should reject the upload with the errors the case update reports")
    void submitShouldReturnUploadLimitErrors() {
        PTCase caseData = getTestPTCase();
        when(ptCaseService.updateDocuments(TEST_CASE_REFERENCE, caseData)).thenReturn(List.of("removeFileFirst"));

        SubmitResponse<State> result = callSubmitHandler(caseData);

        assertThat(result.getErrors()).containsExactly("removeFileFirst");
    }

    @Test
    @DisplayName("Should be available to a citizen on a draft application only")
    void shouldBeConfiguredForTheDraftState() {
        assertThat(event.getPreState()).containsExactly(State.AWAITING_SUBMISSION_TO_HMCTS);
        assertThat(event.getPostState()).containsExactly(State.AWAITING_SUBMISSION_TO_HMCTS);
        assertThat(event.getId()).isEqualTo("citizen-upload-document");
    }
}
