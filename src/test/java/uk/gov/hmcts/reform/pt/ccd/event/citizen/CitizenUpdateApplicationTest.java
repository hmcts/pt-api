package uk.gov.hmcts.reform.pt.ccd.event.citizen;

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
class CitizenUpdateApplicationTest extends BaseEventTest {

    @Mock
    private PTCaseService ptCaseService;

    @BeforeEach
    void setUp() {
        CitizenUpdateApplication underTest = new CitizenUpdateApplication(ptCaseService);
        configureEvent(underTest);
    }

    @Test
    void submitShouldUpdateCaseAndReturnDefaultResponse() {
        PTCase caseData = getTestPTCase();

        SubmitResponse<State> result = callSubmitHandler(caseData);

        verify(ptCaseService).updateCase(TEST_CASE_REFERENCE, caseData);
        assertThat(result).isEqualTo(SubmitResponse.<State>builder().build());
    }
}
