package uk.gov.hmcts.reform.pt.ccd.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ccd.sdk.api.callback.SubmitResponse;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;

import static org.assertj.core.api.Assertions.assertThat;

class CaseReinstateCaseTest extends BaseEventTest {

    @BeforeEach
    void setUp() {
        CaseReinstateCase underTest = new CaseReinstateCase();
        configureEvent(underTest);
    }

    @Test
    void submitShouldReturnDefaultResponse() {
        PTCase caseData = getTestPTCase();

        SubmitResponse<State> result = callSubmitHandler(caseData);

        assertThat(result).isEqualTo(SubmitResponse.<State>builder().build());
    }

    private PTCase getTestPTCase() {
        return PTCase.builder()
            .applicantFirstName("Jane")
            .build();
    }
}
