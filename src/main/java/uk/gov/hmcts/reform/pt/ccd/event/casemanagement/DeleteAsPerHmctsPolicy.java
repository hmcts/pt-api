package uk.gov.hmcts.reform.pt.ccd.event.casemanagement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.DecentralisedConfigBuilder;
import uk.gov.hmcts.ccd.sdk.api.EventPayload;
import uk.gov.hmcts.ccd.sdk.api.callback.SubmitResponse;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.ccd.domain.UserRole;

import static uk.gov.hmcts.ccd.sdk.api.Permission.CRU;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.DRAFT_DISCARDED;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.REQUESTED_FOR_DELETION;
import static uk.gov.hmcts.reform.pt.ccd.domain.UserRole.PT_CASE_WORKER;
import static uk.gov.hmcts.reform.pt.ccd.event.EventId.SYSTEM_DELETE_AS_PER_HMCTS_POLICY;

@Component
@RequiredArgsConstructor
public class DeleteAsPerHmctsPolicy implements CCDConfig<PTCase, State, UserRole> {

    @Override
    public void configureDecentralised(DecentralisedConfigBuilder<PTCase, State, UserRole> configBuilder) {
        configBuilder
            .decentralisedEvent(SYSTEM_DELETE_AS_PER_HMCTS_POLICY.getId(), this::submit)
            .forStates(REQUESTED_FOR_DELETION, DRAFT_DISCARDED)
            .showSummary()
            .name(SYSTEM_DELETE_AS_PER_HMCTS_POLICY.getName())
            .description(SYSTEM_DELETE_AS_PER_HMCTS_POLICY.getName())
            .grant(CRU, PT_CASE_WORKER); // TODO: use correct roles when further details are released
    }

    private SubmitResponse<State> submit(EventPayload<PTCase, State> eventPayload) {
        // TODO: implement when further details are released
        return SubmitResponse.<State>builder().build();
    }
}
