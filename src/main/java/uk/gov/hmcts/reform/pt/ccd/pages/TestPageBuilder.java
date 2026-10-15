package uk.gov.hmcts.reform.pt.ccd.pages;

import lombok.NoArgsConstructor;
import uk.gov.hmcts.ccd.sdk.api.Event;
import uk.gov.hmcts.reform.pt.ccd.domain.LandlordDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole;

@NoArgsConstructor
public class TestPageBuilder {
    public static void createTestCase(Event.EventBuilder<PTCase, UserRole, State> eventBuilder) {
        eventBuilder
            .fields()
            .page("id")
            .pageLabel("testPage")
            .mandatory(PTCase::getApplicantFirstName)
            .mandatory(PTCase::getApplicantLastName);
    }

    public static void updateTestCase(Event.EventBuilder<PTCase, UserRole, State> eventBuilder) {
        eventBuilder
            .fields()
            .page("updateTestCase")
            .pageLabel("Update test case")
            .mandatory(PTCase::getApplicantFirstName)
            .mandatory(PTCase::getApplicantLastName)
            .complex(PTCase::getPropertyDetails)
            .mandatory(PropertyDetails::getAddressLine1)
            .mandatory(PropertyDetails::getAddressLine2)
            .mandatory(PropertyDetails::getPostTown)
            .mandatory(PropertyDetails::getCounty)
            .mandatory(PropertyDetails::getPostcode)
            .done()
            .complex(PTCase::getLandlordDetails)
            .mandatory(LandlordDetails::getRepresentativeType)
            .complex(LandlordDetails::getLandlordPartyDetails)
            .mandatory(PartyDetails::getFirstName)
            .mandatory(PartyDetails::getLastName);
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
