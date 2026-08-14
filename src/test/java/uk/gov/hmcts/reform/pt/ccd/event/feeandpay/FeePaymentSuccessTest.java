package uk.gov.hmcts.reform.pt.ccd.event.feeandpay;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.api.callback.SubmitResponse;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.ccd.event.BaseEventTest;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class FeePaymentSuccessTest extends BaseEventTest {

    @BeforeEach
    void setUp() {
        FeePaymentSuccess underTest = new FeePaymentSuccess();
        configureEvent(underTest);
    }

    @Test
    void shouldReturnCaseIssuedResponse() {
        PTCase caseData = getTestPTCase();

        SubmitResponse<State> result = callSubmitHandler(caseData);

        assertThat(result).isEqualTo(SubmitResponse.<State>builder().state(State.CASE_ISSUED).build());
    }
}
