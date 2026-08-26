package uk.gov.hmcts.reform.pt.ccd.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.gov.hmcts.ccd.sdk.api.HasLabel;

@Getter
@RequiredArgsConstructor
public enum DocumentType implements HasLabel {
    @JsonProperty("hardshipEvidence")
    HARDSHIP_EVIDENCE("Hardship evidence"),

    @JsonProperty("tenancyAgreement")
    TENANCY_AGREEMENT("Tenancy agreement"),

    @JsonProperty("newRentIncreaseNotice")
    NEW_RENT_INCREASE_NOTICE("New rent increase notice"),

    @JsonProperty("noticeNotLegallyValidEvidence")
    NOTICE_NOT_LEGALLY_VALID_EVIDENCE("Notice not legally valid evidence"),

    @JsonProperty("floorPlan")
    FLOOR_PLAN("Floor plan"),

    @JsonProperty("outsideProperty")
    OUTSIDE_PROPERTY("Outside property"),

    @JsonProperty("propertyRooms")
    PROPERTY_ROOMS("Property rooms"),

    @JsonProperty("repairsEvidence")
    REPAIRS_EVIDENCE("Repairs evidence"),

    @JsonProperty("proposedRentEvidence")
    PROPOSED_RENT_EVIDENCE("Proposed rent evidence");

    private final String label;
}
