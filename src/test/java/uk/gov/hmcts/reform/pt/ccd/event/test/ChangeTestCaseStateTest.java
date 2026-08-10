package uk.gov.hmcts.reform.pt.ccd.event.test;

import org.junit.jupiter.api.BeforeEach;
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
class ChangeTestCaseStateTest extends BaseEventTest {

    @Mock
    private PTCaseService ptCaseService;

    @BeforeEach
    void setUp() {
        ChangeTestCaseState underTest = new ChangeTestCaseState(ptCaseService);
        configureEvent(underTest);
    }

    @Test
    void submitShouldUpdateCaseAndReturnResponseWithTargetState() {
        PTCase caseData = getTestPTCase();
        caseData.setTargetState(State.CASE_ISSUED);

        SubmitResponse<State> result = callSubmitHandler(caseData);

        verify(ptCaseService).updateCase(TEST_CASE_REFERENCE, caseData);
        assertThat(result).isEqualTo(SubmitResponse.<State>builder().state(State.CASE_ISSUED).build());
    }
}
