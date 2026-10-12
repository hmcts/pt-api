package uk.gov.hmcts.reform.pt.ccd.event.test;

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
import uk.gov.hmcts.reform.pt.ccd.domain.UserRole;
import uk.gov.hmcts.reform.pt.ccd.pages.TestPageBuilder;
import uk.gov.hmcts.reform.pt.service.PTCaseService;

import static uk.gov.hmcts.reform.pt.ccd.event.EventId.UPDATE_TEST_CASE;

@Component
@RequiredArgsConstructor
public class UpdateTestCase implements CCDConfig<PTCase, State, UserRole> {
    private final PTCaseService ptCaseService;

    @Override
    public void configureDecentralised(DecentralisedConfigBuilder<PTCase, State, UserRole> configBuilder) {
        Event.EventBuilder<PTCase, UserRole, State> eventBuilder = configBuilder
            .decentralisedEvent(UPDATE_TEST_CASE.getId(), this::submit)
            .forAllStates()
            .name(UPDATE_TEST_CASE.getName())
            .grant(Permission.CRU, UserRole.PT_CASE_WORKER, UserRole.SUPER_USER)
            .showSummary()
            .endButtonLabel("Submit");
        TestPageBuilder.updateTestCase(eventBuilder);
    }

    private SubmitResponse<State> submit(EventPayload<PTCase, State> eventPayload) {
        ptCaseService.updateCase(eventPayload.caseReference(), eventPayload.caseData());
        return SubmitResponse.<State>builder().build();
    }
}
