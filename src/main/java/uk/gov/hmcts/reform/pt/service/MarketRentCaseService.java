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

import java.math.BigDecimal;

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
        marketRentCase.setRentCostWeekly(BigDecimal.valueOf(details.getRentCostWeekly()));
        marketRentCase.setRentCostFortnightly(BigDecimal.valueOf(details.getRentCostFortnightly()));
        marketRentCase.setRentCostMonthly(BigDecimal.valueOf(details.getRentCostMonthly()));
        marketRentCase.setRentCostYearly(BigDecimal.valueOf(details.getRentCostYearly()));
        marketRentCase.setRentIncludesCouncilTax(details.getRentIncludesCouncilTax());
        marketRentCase.setCouncilTaxFrequency(details.getCouncilTaxFrequency());
        marketRentCase.setCouncilTaxCostWeekly(BigDecimal.valueOf(details.getCouncilTaxCostWeekly()));
        marketRentCase.setCouncilTaxCostFortnightly(BigDecimal.valueOf(details.getCouncilTaxCostFortnightly()));
        marketRentCase.setCouncilTaxCostMonthly(BigDecimal.valueOf(details.getCouncilTaxCostMonthly()));
        marketRentCase.setCouncilTaxCostYearly(BigDecimal.valueOf(details.getCouncilTaxCostYearly()));
        marketRentCase.setCouncilTaxFrequencyAndCostDetails(details.getCouncilTaxFrequencyAndCostDetails());
        marketRentCase.setUtilitiesPaidFrequency(details.getUtilitiesPaidFrequency());
        marketRentCase.setUtilitiesCostWeekly(BigDecimal.valueOf(details.getUtilitiesPaidCostWeekly()));
        marketRentCase.setUtilitiesCostFortnightly(BigDecimal.valueOf(details.getUtilitiesPaidCostFortnightly()));
        marketRentCase.setUtilitiesCostMonthly(BigDecimal.valueOf(details.getUtilitiesPaidCostMonthly()));
        marketRentCase.setUtilitiesCostYearly(BigDecimal.valueOf(details.getUtilitiesPaidCostYearly()));
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

        marketRentCase.setApplicantSuggestedMonthlyMarketRent(BigDecimal.valueOf(details.getApplicantSuggestedMonthlyMarketRent()));
        marketRentCase.setApplicantSuggestedMonthlyMarketRentReasons(
            details.getApplicantSuggestedMonthlyMarketRentReasons());
        marketRentCase.setAdditionalPropertyInfoToConsiderWhenDeterminingRent(
            details.getAdditionalInfoToConsiderWhenDeterminingRent());
        marketRentCase.setAdditionalPropertyInfoToConsiderWhenDeterminingRentDetails(
            details.getAdditionalInfoToConsiderWhenDeterminingRentDetails());

        marketRentCaseRepository.save(marketRentCase);
    }
}
