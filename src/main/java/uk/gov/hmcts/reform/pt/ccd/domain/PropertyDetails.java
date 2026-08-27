package uk.gov.hmcts.reform.pt.ccd.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.CitizenAccess;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class PropertyDetails {
    @CCD(
        label = "Address line 1",
        access = {CitizenAccess.class}
    )
    private String addressLine1;

    @CCD(
        label = "Address line 2",
        access = {CitizenAccess.class}
    )
    private String addressLine2;

    @CCD(
        label = "Town/City",
        access = {CitizenAccess.class}
    )
    private String postTown;

    @CCD(
        label = "County",
        access = {CitizenAccess.class}
    )
    private String county;

    @CCD(
        label = "Postcode",
        access = {CitizenAccess.class}
    )
    private String postcode;

    @CCD(
        label = "Property type",
        access = {CitizenAccess.class}
    )
    private PropertyType propertyType;

    @CCD(
        label = "Details about the flat being rented",
        access = {CitizenAccess.class}
    )
    private String rentingFlatDetails;

    @CCD(
        label = "Details about the room being rented",
        access = {CitizenAccess.class}
    )
    private String rentingRoomDetails;

    @CCD(
        label = "Details about renting via other method",
        access = {CitizenAccess.class}
    )
    private String otherMethodRentingDetails;

    @CCD(
        label = "Is there a property floor plan available",
        access = {CitizenAccess.class}
    )
    private YesOrNo propertyFloorPlanAvailable;

    @CCD(
        label = "Floor plan manual details",
        access = {CitizenAccess.class}
    )
    private String floorPlanManualDetails;

    @CCD(
        label = "Floor plan document",
        access = {CitizenAccess.class}
    )
    private UploadedDocument floorPlanDocument;

    @CCD(
        label = "Indoor features of the property",
        access = {CitizenAccess.class}
    )
    private String indoorFeatures;

    @CCD(
        label = "Has other facilities",
        access = {CitizenAccess.class}
    )
    private YesOrNo otherFacilitiesAvailable;

    @CCD(
        label = "Other facilities of the property",
        access = {CitizenAccess.class}
    )
    private String otherFacilitiesDetails;

    @CCD(
        label = "Outside property document",
        access = {CitizenAccess.class}
    )
    private UploadedDocument outsidePropertyDocument;

    @CCD(
        label = "Property rooms documents",
        access = {CitizenAccess.class}
    )
    @Builder.Default
    private List<UploadedDocument> roomsDocuments = new ArrayList<>();

    @CCD(
        label = "Has furniture provided in tenancy",
        access = {CitizenAccess.class}
    )
    private YesOrNo furnitureProvidedInTenancy;

    @CCD(
        label = "Furniture provided in tenancy details",
        access = {CitizenAccess.class}
    )
    private String furnitureProvidedInTenancyDetails;

    @CCD(
        label = "Has additional services provided in tenancy",
        access = {CitizenAccess.class}
    )
    private YesOrNo additionalServicesProvidedInTenancy;

    @CCD(
        label = "Additional services provided in tenancy details",
        access = {CitizenAccess.class}
    )
    private String additionalServicesProvidedInTenancyDetails;

    @CCD(
        label = "Tenant shares property with landlord",
        access = {CitizenAccess.class}
    )
    private YesOrNo sharePropertyWithLandlord;

    @CCD(
        label = "Property shared with landlord details",
        access = {CitizenAccess.class}
    )
    private String sharePropertyWithLandlordDetails;

    @CCD(
        label = "Landlord repairs details",
        access = {CitizenAccess.class}
    )
    private String landlordRepairsDetails;

    @CCD(
        label = "Tenant repairs details",
        access = {CitizenAccess.class}
    )
    private String tenantRepairsDetails;

    @CCD(
        label = "Have any tenants made property repairs",
        access = {CitizenAccess.class}
    )
    private YesNoNotSure anyTenantsMadePropertyRepairs;

    @CCD(
        label = "Have any tenants made property repairs details",
        access = {CitizenAccess.class}
    )
    private UploadedDocument repairsEvidenceDocument;
}
