package uk.gov.hmcts.reform.pt.ccd.event;

import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.DecentralisedConfigBuilder;
import uk.gov.hmcts.ccd.sdk.api.EventPayload;
import uk.gov.hmcts.ccd.sdk.api.callback.SubmitResponse;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.ccd.domain.UserRole;

import static uk.gov.hmcts.ccd.sdk.api.Permission.CRU;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.AWAITING_SUBMISSION_TO_HMCTS;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.PENDING_CASE_ISSUED;
import static uk.gov.hmcts.reform.pt.ccd.domain.UserRole.PT_CASE_WORKER;
import static uk.gov.hmcts.reform.pt.ccd.event.EventId.RESUME_DRAFT_CASE;

public class ResumeDraftCase implements CCDConfig<PTCase, State, UserRole> {
    @Override
    public void configureDecentralised(DecentralisedConfigBuilder<PTCase, State, UserRole> configBuilder) {
        configBuilder
            .decentralisedEvent(RESUME_DRAFT_CASE.getId(), this::submit)
            .forStates(AWAITING_SUBMISSION_TO_HMCTS, PENDING_CASE_ISSUED)
            .showSummary()
            .name(RESUME_DRAFT_CASE.getName())
            .grant(CRU, PT_CASE_WORKER); // TODO: use correct roles when further details are released
    }

    private SubmitResponse<State> submit(EventPayload<PTCase, State> eventPayload) {
        // TODO: implement when further details are released
        return SubmitResponse.<State>builder().build();
    }
}
