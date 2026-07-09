package uk.gov.hmcts.reform.pt.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.Event;
import uk.gov.hmcts.ccd.sdk.api.DecentralisedConfigBuilder;
import uk.gov.hmcts.ccd.sdk.api.EventPayload;
import uk.gov.hmcts.ccd.sdk.api.Permission;
import uk.gov.hmcts.ccd.sdk.api.callback.SubmitResponse;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.ccd.domain.UserRole;
import uk.gov.hmcts.reform.pt.ccd.event.EventId;
import uk.gov.hmcts.reform.pt.pages.TestPageBuilder;
import uk.gov.hmcts.reform.pt.service.PTCaseService;

@Component
@RequiredArgsConstructor
public class CreateTestCase implements CCDConfig<PTCase, State, UserRole> {

    private static final String EVENT_NAME = "Create Test Case";

    private final PTCaseService ptCaseService;

    @Override
    public void configureDecentralised(DecentralisedConfigBuilder<PTCase, State, UserRole> configBuilder) {
        Event.EventBuilder<PTCase, UserRole, State> eventBuilder = configBuilder
            .decentralisedEvent(EventId.createTestCase.name(), this::submit, this::start)
            .initialState(State.AWAITING_SUBMISSION_TO_HMCTS)
            .showSummary()
            .name(EVENT_NAME)
            .grant(Permission.CRUD, UserRole.CASE_WORKER);
        TestPageBuilder.createTestCase(eventBuilder);
    }

    private PTCase start(EventPayload<PTCase, State> eventPayload) {
        return eventPayload.caseData();
    }

    private SubmitResponse<State> submit(EventPayload<PTCase, State> eventPayload) {
        // TODO do we need to pass a userId here?
        ptCaseService.createCase(eventPayload.caseReference(), null, eventPayload.caseData());
        return SubmitResponse.<State>builder().state(State.CASE_ISSUED).build();
    }
}
