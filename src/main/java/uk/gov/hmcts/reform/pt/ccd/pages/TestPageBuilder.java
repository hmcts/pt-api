package uk.gov.hmcts.reform.pt.ccd.pages;

import uk.gov.hmcts.ccd.sdk.api.Event;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.ccd.domain.UserRole;

public class TestPageBuilder {
    public static void createTestCase(Event.EventBuilder<PTCase, UserRole, State> eventBuilder) {
        eventBuilder
            .fields()
            .page("id")
            .pageLabel("testPage")
            .mandatory(PTCase::getApplicantFirstName);
    }

    public static void changeCaseState(Event.EventBuilder<PTCase, UserRole, State> eventBuilder) {
        eventBuilder
            .fields()
            .page("changeCaseStatePage")
            .pageLabel("Change Case State")
            .label("changeCaseStatePage-lineSeparator", "---")
            .label("changeCaseState-currentState", "Current state: ${[STATE]}")
            .mandatory(PTCase::getTargetState);
    }
}
