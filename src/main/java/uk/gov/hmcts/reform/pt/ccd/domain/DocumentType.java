package uk.gov.hmcts.reform.pt.ccd.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.gov.hmcts.ccd.sdk.api.HasLabel;

import static uk.gov.hmcts.reform.pt.ccd.domain.DocumentAssociation.PROPERTY_DETAILS;
import static uk.gov.hmcts.reform.pt.ccd.domain.DocumentAssociation.RESPONDENT_PROPERTY_DETAILS;

@Getter
@RequiredArgsConstructor
public enum DocumentType implements HasLabel {
    @JsonProperty("hardshipEvidence")
    HARDSHIP_EVIDENCE("Hardship evidence", PROPERTY_DETAILS),

    @JsonProperty("tenancyAgreement")
    TENANCY_AGREEMENT("Tenancy agreement", PROPERTY_DETAILS),

    @JsonProperty("newRentIncreaseNotice")
    NEW_RENT_INCREASE_NOTICE("New rent increase notice", PROPERTY_DETAILS),

    @JsonProperty("noticeNotLegallyValidEvidence")
    NOTICE_NOT_LEGALLY_VALID_EVIDENCE("Notice not legally valid evidence", PROPERTY_DETAILS),

    @JsonProperty("propertyFloorPlan")
    PROPERTY_FLOOR_PLAN("Property floor plan", PROPERTY_DETAILS),

    @JsonProperty("outsideProperty")
    OUTSIDE_PROPERTY("Outside property", PROPERTY_DETAILS),

    @JsonProperty("propertyRooms")
    PROPERTY_ROOMS("Property rooms", PROPERTY_DETAILS),

    @JsonProperty("tenantRepairsEvidence")
    TENANT_REPAIRS_EVIDENCE("Tenant repairs evidence", PROPERTY_DETAILS),

    @JsonProperty("tenantProposedRentEvidence")
    TENANT_PROPOSED_MARKET_RENT_EVIDENCE("Tenant proposed rent evidence", PROPERTY_DETAILS),

    @JsonProperty("evidenceSupportingResponseToTenancyDetails")
    EVIDENCE_SUPPORTING_RESPONSE_TO_TENANCY_DETAILS(
        "Evidence supporting response to tenancy details", RESPONDENT_PROPERTY_DETAILS),

    @JsonProperty("evidenceSupportingResponseToPropertyDetails")
    EVIDENCE_SUPPORTING_RESPONSE_TO_PROPERTY_DETAILS(
        "Evidence supporting response to property details", RESPONDENT_PROPERTY_DETAILS),

    @JsonProperty("evidenceSupportingRespondentProposedRentAndReasons")
    EVIDENCE_SUPPORTING_RESPONDENT_PROPOSED_RENT_AND_REASONS(
        "Evidence supporting respondent proposed rent and reasons", RESPONDENT_PROPERTY_DETAILS),

    @JsonProperty("evidenceSupportingResponseToTenantExperiencingHardship")
    EVIDENCE_SUPPORTING_RESPONSE_TO_TENANT_EXPERIENCING_HARDSHIP(
        "Evidence supporting response to tenant experiencing hardship", RESPONDENT_PROPERTY_DETAILS),

    @JsonProperty("landlordProposedMarketRentEvidence")
    LANDLORD_PROPOSED_MARKET_RENT_EVIDENCE(
        "Landlord proposed market rent evidence", RESPONDENT_PROPERTY_DETAILS),

    @JsonProperty("landlordRepairsEvidence")
    LANDLORD_REPAIRS_EVIDENCE(
        "Evidence supporting landlord repairs", RESPONDENT_PROPERTY_DETAILS);

    private final String label;
    private final DocumentAssociation association;
}
