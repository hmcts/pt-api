package uk.gov.hmcts.reform.pt.ccd.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.DecentralisedConfigBuilder;
import uk.gov.hmcts.ccd.sdk.api.EventPayload;
import uk.gov.hmcts.ccd.sdk.api.callback.SubmitResponse;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.ccd.domain.UserRole;
import uk.gov.hmcts.reform.pt.service.PTCaseService;

import static uk.gov.hmcts.ccd.sdk.api.Permission.CRU;
import static uk.gov.hmcts.reform.pt.ccd.domain.UserRole.CITIZEN;
import static uk.gov.hmcts.reform.pt.ccd.event.EventId.CITIZEN_SUBMIT_APPLICATION;

@Component
@RequiredArgsConstructor
public class CitizenSubmitApplication implements CCDConfig<PTCase, State, UserRole> {

    private final PTCaseService ptCaseService;

    @Override
    public void configureDecentralised(DecentralisedConfigBuilder<PTCase, State, UserRole> configBuilder) {
        configBuilder
            .decentralisedEvent(CITIZEN_SUBMIT_APPLICATION.getId(), this::submit, this::start)
            .initialState(State.AWAITING_SUBMISSION_TO_HMCTS)
            .showSummary()
            .name(CITIZEN_SUBMIT_APPLICATION.getName())
            .grant(CRU, CITIZEN);
    }

    private PTCase start(EventPayload<PTCase, State> eventPayload) {
        return eventPayload.caseData();
    }

    private SubmitResponse<State> submit(EventPayload<PTCase, State> eventPayload) {
        ptCaseService.updateCase(eventPayload.caseReference(), eventPayload.caseData());
        return SubmitResponse.<State>builder().build();
    }
}
