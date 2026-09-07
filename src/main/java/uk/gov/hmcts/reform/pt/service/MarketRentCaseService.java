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

import static uk.gov.hmcts.reform.pt.util.NullSafeSetter.setIfNotNull;

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
        setIfNotNull(propertyDetails.getPropertyType(), marketRentCase::setTypeOfPropertyRenting);
        setIfNotNull(propertyDetails.getRentingFlatDetails(), marketRentCase::setRentingFlatDetails);
        setIfNotNull(propertyDetails.getRentingRoomDetails(), marketRentCase::setRentingRoomDetails);
        setIfNotNull(propertyDetails.getOtherMethodRentingDetails(), marketRentCase::setOtherMethodOfRentDetails);
        setIfNotNull(propertyDetails.getPropertyFloorPlanAvailable(), marketRentCase::setPropertyFloorPlanAvailable);
        setIfNotNull(propertyDetails.getFloorPlanManualDetails(), marketRentCase::setFloorplanManualDetails);
        setIfNotNull(propertyDetails.getIndoorFeatures(), marketRentCase::setPropertyIndoorFeatures);
        setIfNotNull(propertyDetails.getSharePropertyWithLandlord(), marketRentCase::setSharePropertyWithLandlord);
        setIfNotNull(
            propertyDetails.getSharePropertyWithLandlordDetails(),
            marketRentCase::setSharePropertyWithLandlordDetails
        );

        marketRentCaseRepository.save(marketRentCase);
    }

    @Transactional
    public void updateWithCurrentRentDetails(PTCaseEntity ptCaseEntity, CurrentRentDetails details) {
        MarketRentCaseEntity marketRentCase = ptCaseEntity.getMarketRentCases().stream()
            .findFirst()
            .orElse(new MarketRentCaseEntity());

        setIfNotNull(details.getRentPaymentFrequency(), marketRentCase::setRentPaymentFrequency);
        setIfNotNull(toBigDecimal(details.getRentCostWeekly()), marketRentCase::setRentCostWeekly);
        setIfNotNull(toBigDecimal(details.getRentCostFortnightly()), marketRentCase::setRentCostFortnightly);
        setIfNotNull(toBigDecimal(details.getRentCostMonthly()), marketRentCase::setRentCostMonthly);
        setIfNotNull(toBigDecimal(details.getRentCostYearly()), marketRentCase::setRentCostYearly);
        setIfNotNull(details.getRentIncludesCouncilTax(), marketRentCase::setRentIncludesCouncilTax);
        setIfNotNull(details.getCouncilTaxFrequency(), marketRentCase::setCouncilTaxFrequency);
        setIfNotNull(toBigDecimal(details.getCouncilTaxCostWeekly()), marketRentCase::setCouncilTaxCostWeekly);
        setIfNotNull(
            toBigDecimal(details.getCouncilTaxCostFortnightly()),
            marketRentCase::setCouncilTaxCostFortnightly
        );
        setIfNotNull(toBigDecimal(details.getCouncilTaxCostMonthly()), marketRentCase::setCouncilTaxCostMonthly);
        setIfNotNull(toBigDecimal(details.getCouncilTaxCostYearly()), marketRentCase::setCouncilTaxCostYearly);

        setIfNotNull(
            details.getCouncilTaxFrequencyAndCostDetails(),
            marketRentCase::setCouncilTaxFrequencyAndCostDetails
        );
        setIfNotNull(details.getUtilitiesPaidFrequency(), marketRentCase::setUtilitiesPaidFrequency);
        setIfNotNull(toBigDecimal(details.getUtilitiesPaidCostWeekly()), marketRentCase::setUtilitiesCostWeekly);
        setIfNotNull(
            toBigDecimal(details.getUtilitiesPaidCostFortnightly()),
            marketRentCase::setUtilitiesCostFortnightly
        );
        setIfNotNull(toBigDecimal(details.getUtilitiesPaidCostMonthly()), marketRentCase::setUtilitiesCostMonthly);
        setIfNotNull(toBigDecimal(details.getUtilitiesPaidCostYearly()), marketRentCase::setUtilitiesCostYearly);
        setIfNotNull(
            details.getUtilitiesPaidFrequencyAndCostDetails(),
            marketRentCase::setUtilitiesFrequencyAndCostDetails
        );
        setIfNotNull(
            details.getAnyOtherHouseholdManagementCharges(),
            marketRentCase::setOtherHouseholdManagementCharges
        );
        setIfNotNull(
            details.getOtherHouseholdManagementChargesDetails(),
            marketRentCase::setOtherHouseholdManagementChargesDetails
        );

        marketRentCaseRepository.save(marketRentCase);
    }

    @Transactional
    public void updateWithMarketRentDetails(PTCaseEntity ptCaseEntity, MarketRentDetails details) {
        MarketRentCaseEntity marketRentCase = ptCaseEntity.getMarketRentCases().stream()
            .findFirst()
            .orElse(new MarketRentCaseEntity());

        setIfNotNull(
            toBigDecimal(details.getApplicantSuggestedMonthlyMarketRent()),
            marketRentCase::setApplicantSuggestedMonthlyMarketRent
        );
        setIfNotNull(
            details.getApplicantSuggestedMonthlyMarketRentReasons(),
            marketRentCase::setApplicantSuggestedMonthlyMarketRentReasons
        );
        setIfNotNull(
            details.getAdditionalInfoToConsiderWhenDeterminingRent(),
            marketRentCase::setAdditionalPropertyInfoToConsiderWhenDeterminingRent
        );
        setIfNotNull(
            details.getAdditionalInfoToConsiderWhenDeterminingRentDetails(),
            marketRentCase::setAdditionalPropertyInfoToConsiderWhenDeterminingRentDetails
        );

        marketRentCaseRepository.save(marketRentCase);
    }

    private BigDecimal toBigDecimal(Double value) {
        return value != null ? BigDecimal.valueOf(value) : null;
    }
}
