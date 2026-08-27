package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.CurrentRentDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.MarketRentDetails;
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

    @Transactional
    public void updateWithCurrentRentDetails(PTCaseEntity ptCaseEntity, CurrentRentDetails details) {
        MarketRentCaseEntity marketRentCase = ptCaseEntity.getMarketRentCases().stream()
            .findFirst()
            .orElse(new MarketRentCaseEntity());

        marketRentCase.setRentPaymentFrequency(details.getRentPaymentFrequency());
        marketRentCase.setRentCostWeekly(details.getRentCostWeekly());
        marketRentCase.setRentCostFortnightly(details.getRentCostFortnightly());
        marketRentCase.setRentCostMonthly(details.getRentCostMonthly());
        marketRentCase.setRentCostYearly(details.getRentCostYearly());
        marketRentCase.setRentIncludesCouncilTax(details.getRentIncludesCouncilTax());
        marketRentCase.setCouncilTaxFrequency(details.getCouncilTaxFrequency());
        marketRentCase.setCouncilTaxCostWeekly(details.getCouncilTaxCostWeekly());
        marketRentCase.setCouncilTaxCostFortnightly(details.getCouncilTaxCostFortnightly());
        marketRentCase.setCouncilTaxCostMonthly(details.getCouncilTaxCostMonthly());
        marketRentCase.setCouncilTaxCostYearly(details.getCouncilTaxCostYearly());
        marketRentCase.setCouncilTaxFrequencyAndCostDetails(details.getCouncilTaxFrequencyAndCostDetails());
        marketRentCase.setUtilitiesPaidFrequency(details.getUtilitiesPaidFrequency());
        marketRentCase.setUtilitiesCostWeekly(details.getUtilitiesPaidCostWeekly());
        marketRentCase.setUtilitiesCostFortnightly(details.getUtilitiesPaidCostFortnightly());
        marketRentCase.setUtilitiesCostMonthly(details.getUtilitiesPaidCostMonthly());
        marketRentCase.setUtilitiesCostYearly(details.getUtilitiesPaidCostYearly());
        marketRentCase.setUtilitiesFrequencyAndCostDetails(details.getUtilitiesPaidFrequencyAndCostDetails());
        marketRentCase.setOtherHouseholdManagementCharges(details.getAnyOtherHouseholdManagementCharges());
        marketRentCase.setOtherHouseholdManagementChargesDetails(details.getOtherHouseholdManagementChargesDetails());

        marketRentCaseRepository.save(marketRentCase);
    }

    @Transactional
    public void updateWithMarketRentDetails(PTCaseEntity ptCaseEntity, MarketRentDetails details) {
        MarketRentCaseEntity marketRentCase = ptCaseEntity.getMarketRentCases().stream()
            .findFirst()
            .orElse(new MarketRentCaseEntity());

        marketRentCase.setApplicantSuggestedMonthlyMarketRent(details.getApplicantSuggestedMonthlyMarketRent());
        marketRentCase.setApplicantSuggestedMonthlyMarketRentReasons(
            details.getApplicantSuggestedMonthlyMarketRentReasons());
        marketRentCase.setAdditionalPropertyInfoToConsiderWhenDeterminingRent(
            details.getAdditionalPropertyInfoToConsiderWhenDetermining());
        marketRentCase.setAdditionalPropertyInfoToConsiderWhenDeterminingRentDetails(
            details.getAdditionalPropertyInfoToConsiderWhenDeterminingDetails());

        marketRentCaseRepository.save(marketRentCase);
    }
}
