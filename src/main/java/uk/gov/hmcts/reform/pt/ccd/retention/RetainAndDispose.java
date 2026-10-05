package uk.gov.hmcts.reform.pt.ccd.retention;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.DecentralisedConfigBuilder;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.SystemUserAccess;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.ccd.domain.UserRole;

import static uk.gov.hmcts.ccd.sdk.RetainAndDisposePolicy.CONFIRM_DISPOSAL_EVENT_ID;
import static uk.gov.hmcts.ccd.sdk.RetainAndDisposePolicy.DISPOSAL_EVENT_ID;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.AWAITING_SUBMISSION_TO_HMCTS;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.PendingDisposal;

/**
 * System events driven by the SDK's retain and dispose task. The ids are pinned by
 * RetainAndDisposePolicy and must not be renamed; the task triggers them through ccd-data-store,
 * so they have to exist in the PT definition even though no citizen or caseworker ever sees them.
 */
@Component
public class RetainAndDispose implements CCDConfig<PTCase, State, UserRole> {

    private static final int RESOLVE_TTL_NOW = 0;

    @Override
    public void configureDecentralised(DecentralisedConfigBuilder<PTCase, State, UserRole> configBuilder) {
        configBuilder
            .event(DISPOSAL_EVENT_ID)
            .forStateTransition(AWAITING_SUBMISSION_TO_HMCTS, PendingDisposal)
            .name("Mark for disposal")
            .description("Move a case eligible for disposal into its holding state")
            .grant(new SystemUserAccess());

        configBuilder
            .event(CONFIRM_DISPOSAL_EVENT_ID)
            .forStateTransition(PendingDisposal, PendingDisposal)
            .ttlIncrement(RESOLVE_TTL_NOW)
            .name("Confirm disposal")
            .description("Resolve the disposal TTL once the case has been verified as readable")
            .grant(new SystemUserAccess());
    }
}
