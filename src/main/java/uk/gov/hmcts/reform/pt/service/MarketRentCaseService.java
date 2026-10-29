package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicationType;
import uk.gov.hmcts.reform.pt.ccd.domain.RentDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.HearingPropertyInspectionDetails;
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
        MarketRentCaseEntity marketRentCase = findOrBuildMarketRentCase(ptCaseEntity);
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
    public void updateWithRentDetails(PTCaseEntity ptCaseEntity, RentDetails details) {
        MarketRentCaseEntity marketRentCase = findOrBuildMarketRentCase(ptCaseEntity);
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
            details.getRentInclusiveOfUtilityCharges(),
            marketRentCase::setRentInclusiveOfUtilityCharges
        );
        setIfNotNull(
            details.getAnyOtherHouseholdManagementCharges(),
            marketRentCase::setOtherHouseholdManagementCharges
        );
        setIfNotNull(
            details.getOtherHouseholdManagementChargesDetails(),
            marketRentCase::setOtherHouseholdManagementChargesDetails
        );
        setIfNotNull(
            details.getAdditionalRentalServiceChargesVary(),
            marketRentCase::setAdditionalRentalServiceChargesVary
        );
        setIfNotNull(
            details.getVaryingAdditionalRentalServiceChargesDetails(),
            marketRentCase::setVaryingAdditionalRentalServiceChargesDetails
        );
        setIfNotNull(
            toBigDecimal(details.getApplicantSuggestedMarketRent()),
            marketRentCase::setApplicantSuggestedMarketRent
        );
        setIfNotNull(
            details.getApplicantSuggestedMarketRentReasons(),
            marketRentCase::setApplicantSuggestedMarketRentReasons
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

    @Transactional
    public void updateWithPropertyInspectionDetails(
        PTCaseEntity ptCaseEntity, HearingPropertyInspectionDetails inspectionDetails
    ) {
        MarketRentCaseEntity marketRentCase = findOrBuildMarketRentCase(ptCaseEntity);
        setIfNotNull(
            inspectionDetails.getAgreeToDecisionWithoutInspection(),
            marketRentCase::setAgreeToDecisionWithoutInspection
        );
        setIfNotNull(
            inspectionDetails.getNoDecisionWithoutInspectionReason(),
            marketRentCase::setNoDecisionWithoutInspectionReason
        );
        setIfNotNull(inspectionDetails.getHearingRequested(), marketRentCase::setHearingRequested);
        setIfNotNull(inspectionDetails.getReasonHearingRequested(), marketRentCase::setReasonHearingRequested);

        marketRentCaseRepository.save(marketRentCase);
    }

    @Transactional
    public void updateWithApplicationType(PTCaseEntity ptCaseEntity, ApplicationType applicationType) {
        MarketRentCaseEntity marketRentCase = findOrBuildMarketRentCase(ptCaseEntity);
        setIfNotNull(applicationType, marketRentCase::setMarketRentCaseType);

        marketRentCaseRepository.save(marketRentCase);
    }

    @Transactional
    public MarketRentCaseEntity getOrCreateMarketRentCase(PTCaseEntity ptCaseEntity) {
        MarketRentCaseEntity marketRentCase = findOrBuildMarketRentCase(ptCaseEntity);
        marketRentCaseRepository.save(marketRentCase);
        return marketRentCase;
    }

    private MarketRentCaseEntity findOrBuildMarketRentCase(PTCaseEntity ptCaseEntity) {
        MarketRentCaseEntity marketRentCase = ptCaseEntity.getMarketRentCase();
        if (marketRentCase == null) {
            marketRentCase = new MarketRentCaseEntity();
            ptCaseEntity.setMarketRentCase(marketRentCase);
        }
        marketRentCase.setPtCase(ptCaseEntity);
        return marketRentCase;
    }

    private BigDecimal toBigDecimal(Double value) {
        return value != null ? BigDecimal.valueOf(value) : null;
    }
}
