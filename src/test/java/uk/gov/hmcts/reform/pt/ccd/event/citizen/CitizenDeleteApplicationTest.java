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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class CitizenDeleteApplicationTest extends BaseEventTest {

    @Mock
    private PTCaseService ptCaseService;

    @BeforeEach
    void setUp() {
        configureEvent(new CitizenDeleteApplication(ptCaseService));
    }

    @Test
    @DisplayName("Should delete the case's documents from CDAM")
    void submitShouldDeleteDocuments() {
        PTCase caseData = getTestPTCase();

        SubmitResponse<State> result = callSubmitHandler(caseData);

        verify(ptCaseService).deleteDocumentsForCase(TEST_CASE_REFERENCE);
        assertThat(result).isEqualTo(SubmitResponse.defaultResponse());
        verifyNoMoreInteractions(ptCaseService);
    }

    @Test
    @DisplayName("Should move a draft application into the disposal holding state")
    void shouldTransitionToPendingDisposal() {
        assertThat(event.getPreState()).containsExactly(State.AWAITING_SUBMISSION_TO_HMCTS);
        assertThat(event.getPostState()).containsExactly(State.PendingDisposal);
        assertThat(event.getId()).isEqualTo("citizen-delete-application");
    }

    @Test
    @DisplayName("Should expire the case's TTL so the disposer collects it")
    void shouldExpireTheTtlImmediately() {
        assertThat(event.getTtlIncrement()).isEqualTo(-1);
    }
}
