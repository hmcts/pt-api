package uk.gov.hmcts.reform.pt.dto;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyType;
import uk.gov.hmcts.reform.pt.ccd.domain.YesNoNotSure;

import java.util.List;

@Data
@Builder
public class PropertyDetailsDto {
    private String addressLine1;
    private String addressLine2;
    private String postTown;
    private String county;
    private String postcode;
    private PropertyType propertyType;
    private String rentingFlatDetails;
    private String rentingRoomDetails;
    private String otherMethodRentingDetails;
    private YesOrNo propertyFloorPlanAvailable;
    private String floorPlanManualDetails;
    private DocumentDto floorPlanDocument;
    private String indoorFeatures;
    private YesOrNo otherFacilitiesAvailable;
    private String otherFacilitiesDetails;
    private DocumentDto outsidePropertyDocument;
    private List<DocumentDto> propertyRoomsDocuments;
    private YesOrNo furnitureProvidedInTenancy;
    private String furnitureProvidedInTenancyDetails;
    private YesOrNo additionalServicesProvidedInTenancy;
    private String additionalServicesProvidedInTenancyDetails;
    private YesOrNo sharePropertyWithLandlord;
    private String sharePropertyWithLandlordDetails;
    private String landlordRepairsDetails;
    private String tenantRepairsDetails;
    private YesNoNotSure anyTenantsMadePropertyRepairs;
    private DocumentDto repairsEvidenceDocument;
}
