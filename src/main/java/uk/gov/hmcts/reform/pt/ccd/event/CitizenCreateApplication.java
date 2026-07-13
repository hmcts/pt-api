package uk.gov.hmcts.reform.pt.ccd.event;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.DecentralisedConfigBuilder;
import uk.gov.hmcts.ccd.sdk.api.EventPayload;
import uk.gov.hmcts.ccd.sdk.api.callback.SubmitResponse;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.ccd.domain.UserRole;
import uk.gov.hmcts.reform.pt.idam.User;
import uk.gov.hmcts.reform.pt.service.PTCaseService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CitizenCreateApplication implements CCDConfig<PTCase, State, UserRole> {

    private static final String EVENT_NAME = "Citizen Create Application";
    private static final UserRole CITIZEN_USER_ROLE = UserRole.CITIZEN;

    private final PTCaseService ptCaseService;

    @Override
    public void configureDecentralised(DecentralisedConfigBuilder<PTCase, State, UserRole> configBuilder) {
        configBuilder
            .decentralisedEvent(EventId.citizenCreateApplication.name(), this::submit, this::start)
            .initialState(State.AWAITING_SUBMISSION_TO_HMCTS)
            .showSummary()
            .name(EVENT_NAME)
            .grant(CITIZEN_USER_ROLE.getCaseTypePermissionsEnum(), CITIZEN_USER_ROLE);
    }

    private PTCase start(EventPayload<PTCase, State> eventPayload) {
        return eventPayload.caseData();
    }

    private SubmitResponse<State> submit(EventPayload<PTCase, State> eventPayload) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID userId = UUID.fromString(user.getUserDetails().getUid());
        ptCaseService.createCase(eventPayload.caseReference(), userId, eventPayload.caseData());
        return SubmitResponse.<State>builder().build();
    }
}
