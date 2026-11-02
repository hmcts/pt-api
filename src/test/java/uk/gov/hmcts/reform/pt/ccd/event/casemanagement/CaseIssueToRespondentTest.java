package uk.gov.hmcts.reform.pt.ccd.event.casemanagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.api.callback.SubmitResponse;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.ccd.event.BaseEventTest;
import uk.gov.hmcts.reform.pt.service.IssueToRespondentService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CaseIssueToRespondentTest extends BaseEventTest {

    @Mock
    private IssueToRespondentService issueToRespondentService;

    @BeforeEach
    void setUp() {
        CaseIssueToRespondent underTest = new CaseIssueToRespondent(issueToRespondentService);
        configureEvent(underTest);
    }

    @Test
    void submitShouldReturnDefaultResponse() {
        PTCase caseData = getTestPTCase();

        SubmitResponse<State> result = callSubmitHandler(caseData);

        verify(issueToRespondentService).issueToRespondent(1234L);
        assertThat(result).isEqualTo(SubmitResponse.<State>builder().build());
    }
}
