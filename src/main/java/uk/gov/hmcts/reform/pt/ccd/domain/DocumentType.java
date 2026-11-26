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

    @JsonProperty("propertyFloorPlan")
    PROPERTY_FLOOR_PLAN("Property floor plan"),

    @JsonProperty("outsideProperty")
    OUTSIDE_PROPERTY("Outside property"),

    @JsonProperty("propertyRooms")
    PROPERTY_ROOMS("Property rooms"),

    @JsonProperty("tenantRepairsEvidence")
    TENANT_REPAIRS_EVIDENCE("Tenant repairs evidence"),

    @JsonProperty("tenantProposedRentEvidence")
    TENANT_PROPOSED_MARKET_RENT_EVIDENCE("Tenant proposed rent evidence"),

    @JsonProperty("evidenceSupportingResponseToTenancyDetails")
    EVIDENCE_SUPPORTING_RESPONSE_TO_TENANCY_DETAILS("Evidence supporting response to tenancy details"),

    @JsonProperty("evidenceSupportingResponseToPropertyDetails")
    EVIDENCE_SUPPORTING_RESPONSE_TO_PROPERTY_DETAILS("Evidence supporting response to property details"),

    @JsonProperty("evidenceSupportingRespondentProposedRentAndReasons")
    EVIDENCE_SUPPORTING_RESPONDENT_PROPOSED_RENT_AND_REASONS(
        "Evidence supporting respondent proposed rent and reasons"),

    @JsonProperty("evidenceSupportingResponseToTenantExperiencingHardship")
    EVIDENCE_SUPPORTING_RESPONSE_TO_TENANT_EXPERIENCING_HARDSHIP(
        "Evidence supporting response to tenant experiencing hardship"),

    @JsonProperty("landlordProposedMarketRentEvidence")
    LANDLORD_PROPOSED_MARKET_RENT_EVIDENCE("Landlord proposed market rent evidence"),

    @JsonProperty("landlordRepairsEvidence")
    LANDLORD_REPAIRS_EVIDENCE("Evidence supporting landlord repairs");

    private final String label;
}
