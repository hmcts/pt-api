package uk.gov.hmcts.reform.pt.ccd.retention;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.RetainAndDisposePolicy;
import uk.gov.hmcts.reform.pt.ccd.CaseType;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.service.PTCaseService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class PtRetainAndDisposePolicy implements RetainAndDisposePolicy {

    private final PTCaseService ptCaseService;

    @Override
    public Set<String> caseTypes() {
        return Set.of(CaseType.getCaseType());
    }

    /**
     * Empty by design. Cases reach PendingDisposal through the citizen event rather than by being
     * marked on a schedule, and the retention task picks them up from that state.
     */
    @Override
    public Collection<Long> findCandidatesForDisposal() {
        return List.of();
    }

    /**
     * Called by the retention task once CCD no longer holds the case, inside the same db transaction
     * as the deletion from ccd.case_data.
     */
    @Override
    public void dispose(long caseReference) {
        try {
            ptCaseService.deleteCase(caseReference);
            log.debug("Deleted pt-api case data for case {}", caseReference);
        } catch (CaseNotFoundException e) {
            log.warn("No pt-api case data found for case {} during disposal", caseReference);
        }
    }
}
