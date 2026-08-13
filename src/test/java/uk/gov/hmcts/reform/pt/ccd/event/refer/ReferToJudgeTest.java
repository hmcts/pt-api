package uk.gov.hmcts.reform.pt.ccd.event.refer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ccd.sdk.api.callback.SubmitResponse;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.ccd.event.BaseEventTest;

import static org.assertj.core.api.Assertions.assertThat;

class ReferToJudgeTest extends BaseEventTest {

    @BeforeEach
    void setUp() {
        ReferToJudge underTest = new ReferToJudge();
        configureEvent(underTest);
    }

    @Test
    void submitShouldReturnDefaultResponse() {
        PTCase caseData = getTestPTCase();

        SubmitResponse<State> result = callSubmitHandler(caseData);

        assertThat(result).isEqualTo(SubmitResponse.<State>builder().build());
    }
}
