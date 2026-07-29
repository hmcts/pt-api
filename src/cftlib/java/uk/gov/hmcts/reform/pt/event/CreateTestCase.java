package uk.gov.hmcts.reform.pt.event;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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
import uk.gov.hmcts.reform.pt.idam.User;
import uk.gov.hmcts.reform.pt.pages.TestPageBuilder;
import uk.gov.hmcts.reform.pt.service.PTCaseService;

import java.util.UUID;

import static uk.gov.hmcts.reform.pt.ccd.event.EventId.CREATE_TEST_CASE;

@Component
@RequiredArgsConstructor
public class CreateTestCase implements CCDConfig<PTCase, State, UserRole> {

    private final PTCaseService ptCaseService;

    @Override
    public void configureDecentralised(DecentralisedConfigBuilder<PTCase, State, UserRole> configBuilder) {
        Event.EventBuilder<PTCase, UserRole, State> eventBuilder = configBuilder
            .decentralisedEvent(CREATE_TEST_CASE.getId(), this::submit, this::start)
            .initialState(State.AWAITING_SUBMISSION_TO_HMCTS)
            .showSummary()
            .name(CREATE_TEST_CASE.getName())
            .grant(Permission.CRUD, UserRole.CASE_WORKER);
        TestPageBuilder.createTestCase(eventBuilder);
    }

    private PTCase start(EventPayload<PTCase, State> eventPayload) {
        return eventPayload.caseData();
    }

    private SubmitResponse<State> submit(EventPayload<PTCase, State> eventPayload) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID userId = UUID.fromString(user.getUserDetails().getUid());
        ptCaseService.createCase(eventPayload.caseReference(), userId, eventPayload.caseData());
        return SubmitResponse.<State>builder().state(State.CASE_ISSUED).build();
    }
}
