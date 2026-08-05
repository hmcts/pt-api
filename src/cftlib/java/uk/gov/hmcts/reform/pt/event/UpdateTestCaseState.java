package uk.gov.hmcts.reform.pt.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.DecentralisedConfigBuilder;
import uk.gov.hmcts.ccd.sdk.api.Event;
import uk.gov.hmcts.ccd.sdk.api.EventPayload;
import uk.gov.hmcts.ccd.sdk.api.Permission;
import uk.gov.hmcts.ccd.sdk.api.callback.SubmitResponse;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole;
import uk.gov.hmcts.reform.pt.pages.TestPageBuilder;
import uk.gov.hmcts.reform.pt.service.PTCaseService;

import static uk.gov.hmcts.reform.pt.ccd.event.EventId.UPDATE_TEST_CASE_STATE;

@Component
@RequiredArgsConstructor
public class UpdateTestCaseState implements CCDConfig<PTCase, State, UserRole> {

    private final PTCaseService ptCaseService;

    @Override
    public void configureDecentralised(DecentralisedConfigBuilder<PTCase, State, UserRole> configBuilder) {
        Event.EventBuilder<PTCase, UserRole, State> eventBuilder = configBuilder
            .decentralisedEvent(UPDATE_TEST_CASE_STATE.getId(), this::submit)
            .forAllStates()
            .name(UPDATE_TEST_CASE_STATE.getName())
            .grant(Permission.CRUD, UserRole.PT_CASE_WORKER)
            .showSummary()
            .endButtonLabel("Submit");
        TestPageBuilder.changeCaseState(eventBuilder);
    }

    private SubmitResponse<State> submit(EventPayload<PTCase, State> eventPayload) {
        ptCaseService.updateCase(eventPayload.caseReference(), eventPayload.caseData());
        return SubmitResponse.<State>builder().state(eventPayload.caseData().getTargetState()).build();
    }
}
