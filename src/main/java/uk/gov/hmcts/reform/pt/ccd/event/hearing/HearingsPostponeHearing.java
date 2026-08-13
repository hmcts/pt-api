package uk.gov.hmcts.reform.pt.ccd.event.hearing;

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
import static uk.gov.hmcts.reform.pt.ccd.domain.State.CASE_PROGRESSION;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.PREPARE_FOR_HEARING_CONDUCT_HEARING;
import static uk.gov.hmcts.reform.pt.ccd.domain.UserRole.PT_CASE_WORKER;
import static uk.gov.hmcts.reform.pt.ccd.event.EventId.HEARINGS_POSTPONE_HEARING;

@Component
@RequiredArgsConstructor
public class HearingsPostponeHearing implements CCDConfig<PTCase, State, UserRole> {

    @Override
    public void configureDecentralised(DecentralisedConfigBuilder<PTCase, State, UserRole> configBuilder) {
        configBuilder
            .decentralisedEvent(HEARINGS_POSTPONE_HEARING.getId(), this::submit)
            .forStateTransition(
                PREPARE_FOR_HEARING_CONDUCT_HEARING,
                EnumSet.of(CASE_PROGRESSION, PREPARE_FOR_HEARING_CONDUCT_HEARING)
            )
            .showSummary()
            .name(HEARINGS_POSTPONE_HEARING.getName())
            .description(HEARINGS_POSTPONE_HEARING.getName())
            .grant(CRU, PT_CASE_WORKER); // TODO: use correct roles when further details are released
    }

    private SubmitResponse<State> submit(EventPayload<PTCase, State> eventPayload) {
        // TODO: implement when further details are released
        return SubmitResponse.<State>builder().build();
    }
}
