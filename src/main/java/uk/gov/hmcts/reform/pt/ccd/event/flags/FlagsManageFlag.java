package uk.gov.hmcts.reform.pt.ccd.event.flags;

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
import static uk.gov.hmcts.reform.pt.ccd.domain.State.CASE_ISSUED;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.CASE_PROGRESSION;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.HEARING_READINESS;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.PENDING_CASE_ISSUED;
import static uk.gov.hmcts.reform.pt.ccd.domain.UserRole.PT_CASE_WORKER;
import static uk.gov.hmcts.reform.pt.ccd.event.EventId.FLAGS_MANAGE_FLAG;

@Component
@RequiredArgsConstructor
public class FlagsManageFlag implements CCDConfig<PTCase, State, UserRole> {

    @Override
    public void configureDecentralised(DecentralisedConfigBuilder<PTCase, State, UserRole> configBuilder) {
        configBuilder
            .decentralisedEvent(FLAGS_MANAGE_FLAG.getId(), this::submit)
            .forStates(PENDING_CASE_ISSUED, CASE_ISSUED, CASE_PROGRESSION, HEARING_READINESS)
            .name(FLAGS_MANAGE_FLAG.getName())
            .description(FLAGS_MANAGE_FLAG.getName())
            .grant(CRU, PT_CASE_WORKER) // TODO: use correct roles when further details are released
            .showSummary()
            .endButtonLabel("Submit");
    }

    private SubmitResponse<State> submit(EventPayload<PTCase, State> eventPayload) {
        // TODO: implement when further details are released
        return SubmitResponse.<State>builder().build();
    }
}
