package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyDetails;
import uk.gov.hmcts.reform.pt.entity.MarketRentCaseEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.MarketRentCaseRepository;

@Service
@RequiredArgsConstructor
public class MarketRentCaseService {

    private final MarketRentCaseRepository marketRentCaseRepository;

    @Transactional
    public void updateWithPropertyDetails(PTCaseEntity ptCaseEntity, PropertyDetails propertyDetails) {
        MarketRentCaseEntity marketRentCase = ptCaseEntity.getMarketRentCases().stream()
            .findFirst()
            .orElse(new MarketRentCaseEntity());

        marketRentCase.setPtCase(ptCaseEntity);
        marketRentCase.setTypeOfPropertyRenting(propertyDetails.getPropertyType());
        marketRentCase.setRentingFlatDetails(propertyDetails.getRentingFlatDetails());
        marketRentCase.setRentingRoomDetails(propertyDetails.getRentingRoomDetails());
        marketRentCase.setOtherMethodOfRentDetails(propertyDetails.getOtherMethodRentingDetails());
        marketRentCase.setPropertyFloorPlanAvailable(propertyDetails.getPropertyFloorPlanAvailable());
        marketRentCase.setFloorplanManualDetails(propertyDetails.getFloorPlanManualDetails());
        marketRentCase.setPropertyIndoorFeatures(propertyDetails.getIndoorFeatures());
        marketRentCase.setSharePropertyWithLandlord(propertyDetails.getSharePropertyWithLandlord());
        marketRentCase.setSharePropertyWithLandlordDetails(propertyDetails.getSharePropertyWithLandlordDetails());

        marketRentCaseRepository.save(marketRentCase);
    }
}
