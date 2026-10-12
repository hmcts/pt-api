package uk.gov.hmcts.reform.pt.ccd.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.gov.hmcts.ccd.sdk.api.HasLabel;

@Getter
@RequiredArgsConstructor
public enum VettingInformationType implements HasLabel {
    SECTION_13_NOTICE("Section 13 notice"),
    LANDLORD_NAME_AND_CONTACT_DETAILS("Landlord's name and contact details"),
    REPRESENTATIONS_WHY_APPLICATION_NOT_OUT_OF_TIME("Representations for why the application is not out of time"),
    TENANCY_AGREEMENT("Tenancy agreement"),
    OTHER("Other documents or information");

    private final String label;
}
