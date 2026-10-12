package uk.gov.hmcts.reform.pt.ccd.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CcdConstants {
    public static final String LINE_SEPARATOR = "---";
    public static final String HINT_SELECT_ALL_THAT_APPLY = "Select all that apply";
    public static final String NEVER_SHOW = "[STATE]=\"NEVER_SHOW\"";

    public static final String CCD_CASE_SUMMARY_HTML =
        """
        <p class="govuk-body">
            Case number: ${[CASE_REFERENCE]}<br/>
            ${propertyDetails.addressLine1}, ${propertyDetails.addressLine2}, \
            ${propertyDetails.postTown}, ${propertyDetails.county}, ${propertyDetails.postcode}<br/>
            ${applicantFirstName} ${applicantLastName} vs ${landlordDetails.landlordPartyDetails.firstName} \
            ${landlordDetails.landlordPartyDetails.lastName}
        </p>
        """;
}
