package uk.gov.hmcts.reform.pt.ccd.event.respondent;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RespondentDeleteDocumentTest extends BaseEventTest {

    @Mock
    private PTCaseService ptCaseService;

    @BeforeEach
    void setUp() {
        configureEvent(new RespondentDeleteDocument(ptCaseService));
    }

    @Test
    @DisplayName("Should remove the respondent document named by the control field")
    void submitShouldDeleteRespondentDocument() {
        PTCase caseData = getTestPTCase();

        SubmitResponse<State> result = callSubmitHandler(caseData);

        verify(ptCaseService).deleteRespondentDocument(TEST_CASE_REFERENCE, caseData);
        assertThat(result).isEqualTo(SubmitResponse.<State>builder().build());
    }

    @Test
    @DisplayName("Should be available to a respondent while the case is in case progression only")
    void shouldBeConfiguredForCaseProgression() {
        assertThat(event.getPreState()).containsExactly(State.CASE_PROGRESSION);
        assertThat(event.getPostState()).containsExactly(State.CASE_PROGRESSION);
        assertThat(event.getId()).isEqualTo("respondent-delete-document");
    }
}
