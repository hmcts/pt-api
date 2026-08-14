package uk.gov.hmcts.reform.pt.ccd.event.draft;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.DecentralisedConfigBuilder;
import uk.gov.hmcts.ccd.sdk.api.EventPayload;
import uk.gov.hmcts.ccd.sdk.api.callback.SubmitResponse;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.ccd.domain.UserRole;

import java.util.EnumSet;

import static uk.gov.hmcts.ccd.sdk.api.Permission.CRU;
import static uk.gov.hmcts.reform.pt.ccd.domain.UserRole.PT_CASE_WORKER;
import static uk.gov.hmcts.reform.pt.ccd.event.EventId.DELETE_DRAFT_CASE;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.AWAITING_SUBMISSION_TO_HMCTS;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.PENDING_CASE_ISSUED;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.REQUESTED_FOR_DELETION;

@Component
@RequiredArgsConstructor
public class DeleteDraftCase implements CCDConfig<PTCase, State, UserRole> {
    @Override
    public void configureDecentralised(DecentralisedConfigBuilder<PTCase, State, UserRole> configBuilder) {
        configBuilder
            .decentralisedEvent(DELETE_DRAFT_CASE.getId(), this::submit)
            .forStateTransition(EnumSet.of(AWAITING_SUBMISSION_TO_HMCTS, PENDING_CASE_ISSUED), REQUESTED_FOR_DELETION)
            .name(DELETE_DRAFT_CASE.getName())
            .grant(CRU, PT_CASE_WORKER) // TODO: use correct roles when further details are released
            .showSummary()
            .endButtonLabel("Submit");
    }

    private SubmitResponse<State> submit(EventPayload<PTCase, State> eventPayload) {
        // TODO: implement when further details are released
        return SubmitResponse.<State>builder().build();
    }
}
