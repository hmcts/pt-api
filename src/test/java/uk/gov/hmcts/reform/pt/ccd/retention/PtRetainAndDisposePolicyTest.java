package uk.gov.hmcts.reform.pt.ccd.retention;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pt.ccd.CaseType;
import uk.gov.hmcts.reform.pt.service.PTCaseService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PtRetainAndDisposePolicyTest {

    private static final long CASE_REFERENCE = 1234567890123456L;

    @Mock
    private PTCaseService ptCaseService;

    @InjectMocks
    private PtRetainAndDisposePolicy policy;

    @Test
    @DisplayName("Should delete this service's case data when the retention task disposes of a case")
    void disposeDeletesTheCase() {
        policy.dispose(CASE_REFERENCE);

        verify(ptCaseService).deleteCase(CASE_REFERENCE);
    }

    @Test
    @DisplayName("Should govern only the PT case type")
    void caseTypesIsPtOnly() {
        assertThat(policy.caseTypes()).containsExactly(CaseType.getCaseType());
    }

    @Test
    @DisplayName("Should offer no candidates - cases are marked by the citizen event, not on a schedule")
    void findCandidatesForDisposalIsEmpty() {
        assertThat(policy.findCandidatesForDisposal()).isEmpty();
    }
}
