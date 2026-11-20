package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicationType;
import uk.gov.hmcts.reform.pt.ccd.domain.RentDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.HearingPropertyInspectionDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyDetails;
import uk.gov.hmcts.reform.pt.entity.CasePropertyDetailsEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.CasePropertyDetailsRepository;

import java.math.BigDecimal;

import static uk.gov.hmcts.reform.pt.util.NullSafeSetter.setIfNotNull;

@Service
@RequiredArgsConstructor
public class CasePropertyDetailsService {

    private final CasePropertyDetailsRepository casePropertyDetailsRepository;

    @Transactional
    public void updateWithPropertyDetails(PTCaseEntity ptCaseEntity, PropertyDetails propertyDetails) {
        CasePropertyDetailsEntity casePropertyDetails = findOrBuildCasePropertyDetails(ptCaseEntity);
        setIfNotNull(propertyDetails.getPropertyType(), casePropertyDetails::setTypeOfPropertyRenting);
        setIfNotNull(propertyDetails.getRentingFlatDetails(), casePropertyDetails::setRentingFlatDetails);
        setIfNotNull(propertyDetails.getRentingRoomDetails(), casePropertyDetails::setRentingRoomDetails);
        setIfNotNull(propertyDetails.getOtherMethodRentingDetails(), casePropertyDetails::setOtherMethodOfRentDetails);
        setIfNotNull(propertyDetails.getPropertyFloorPlanAvailable(),
                     casePropertyDetails::setPropertyFloorPlanAvailable);
        setIfNotNull(propertyDetails.getFloorPlanManualDetails(), casePropertyDetails::setFloorplanManualDetails);
        setIfNotNull(propertyDetails.getIndoorFeatures(), casePropertyDetails::setPropertyIndoorFeatures);
        setIfNotNull(propertyDetails.getSharePropertyWithLandlord(), casePropertyDetails::setSharePropertyWithLandlord);
        setIfNotNull(
            propertyDetails.getSharePropertyWithLandlordDetails(),
            casePropertyDetails::setSharePropertyWithLandlordDetails
        );

        casePropertyDetailsRepository.save(casePropertyDetails);
    }

    @Transactional
    public void updateWithRentDetails(PTCaseEntity ptCaseEntity, RentDetails details) {
        CasePropertyDetailsEntity casePropertyDetails = findOrBuildCasePropertyDetails(ptCaseEntity);
        setIfNotNull(details.getRentPaymentFrequency(), casePropertyDetails::setRentPaymentFrequency);
        setIfNotNull(toBigDecimal(details.getRentCostWeekly()), casePropertyDetails::setRentCostWeekly);
        setIfNotNull(toBigDecimal(details.getRentCostFortnightly()), casePropertyDetails::setRentCostFortnightly);
        setIfNotNull(toBigDecimal(details.getRentCostMonthly()), casePropertyDetails::setRentCostMonthly);
        setIfNotNull(toBigDecimal(details.getRentCostYearly()), casePropertyDetails::setRentCostYearly);
        setIfNotNull(details.getRentIncludesCouncilTax(), casePropertyDetails::setRentIncludesCouncilTax);
        setIfNotNull(details.getCouncilTaxFrequency(), casePropertyDetails::setCouncilTaxFrequency);
        setIfNotNull(toBigDecimal(details.getCouncilTaxCostWeekly()), casePropertyDetails::setCouncilTaxCostWeekly);
        setIfNotNull(
            toBigDecimal(details.getCouncilTaxCostFortnightly()),
            casePropertyDetails::setCouncilTaxCostFortnightly
        );
        setIfNotNull(toBigDecimal(details.getCouncilTaxCostMonthly()), casePropertyDetails::setCouncilTaxCostMonthly);
        setIfNotNull(toBigDecimal(details.getCouncilTaxCostYearly()), casePropertyDetails::setCouncilTaxCostYearly);
        setIfNotNull(
            details.getCouncilTaxFrequencyAndCostDetails(),
            casePropertyDetails::setCouncilTaxFrequencyAndCostDetails
        );
        setIfNotNull(details.getUtilitiesPaidFrequency(), casePropertyDetails::setUtilitiesPaidFrequency);
        setIfNotNull(toBigDecimal(details.getUtilitiesPaidCostWeekly()), casePropertyDetails::setUtilitiesCostWeekly);
        setIfNotNull(
            toBigDecimal(details.getUtilitiesPaidCostFortnightly()),
            casePropertyDetails::setUtilitiesCostFortnightly
        );
        setIfNotNull(toBigDecimal(details.getUtilitiesPaidCostMonthly()), casePropertyDetails::setUtilitiesCostMonthly);
        setIfNotNull(toBigDecimal(details.getUtilitiesPaidCostYearly()), casePropertyDetails::setUtilitiesCostYearly);
        setIfNotNull(
            details.getUtilitiesPaidFrequencyAndCostDetails(),
            casePropertyDetails::setUtilitiesFrequencyAndCostDetails
        );
        setIfNotNull(
            details.getRentInclusiveOfUtilityCharges(),
            casePropertyDetails::setRentInclusiveOfUtilityCharges
        );
        setIfNotNull(
            details.getAnyOtherHouseholdManagementCharges(),
            casePropertyDetails::setOtherHouseholdManagementCharges
        );
        setIfNotNull(
            details.getOtherHouseholdManagementChargesDetails(),
            casePropertyDetails::setOtherHouseholdManagementChargesDetails
        );
        setIfNotNull(
            details.getAdditionalRentalServiceChargesVary(),
            casePropertyDetails::setAdditionalRentalServiceChargesVary
        );
        setIfNotNull(
            details.getVaryingAdditionalRentalServiceChargesDetails(),
            casePropertyDetails::setVaryingAdditionalRentalServiceChargesDetails
        );
        setIfNotNull(
            toBigDecimal(details.getApplicantSuggestedMarketRent()),
            casePropertyDetails::setApplicantSuggestedMarketRent
        );
        setIfNotNull(
            details.getApplicantSuggestedMarketRentFrequency(),
            casePropertyDetails::setApplicantSuggestedMarketRentFrequency
        );
        setIfNotNull(
            details.getApplicantSuggestedMarketRentFrequencyOther(),
            casePropertyDetails::setApplicantSuggestedMarketRentFrequencyOther
        );
        setIfNotNull(
            details.getApplicantSuggestedMarketRentReasons(),
            casePropertyDetails::setApplicantSuggestedMarketRentReasons
        );
        setIfNotNull(
            details.getAdditionalInfoToConsiderWhenDeterminingRent(),
            casePropertyDetails::setAdditionalPropertyInfoToConsiderWhenDeterminingRent
        );
        setIfNotNull(
            details.getAdditionalInfoToConsiderWhenDeterminingRentDetails(),
            casePropertyDetails::setAdditionalPropertyInfoToConsiderWhenDeterminingRentDetails
        );

        casePropertyDetailsRepository.save(casePropertyDetails);
    }

    @Transactional
    public void updateWithPropertyInspectionDetails(
        PTCaseEntity ptCaseEntity, HearingPropertyInspectionDetails inspectionDetails
    ) {
        CasePropertyDetailsEntity casePropertyDetails = findOrBuildCasePropertyDetails(ptCaseEntity);
        setIfNotNull(
            inspectionDetails.getAgreeToDecisionWithoutInspection(),
            casePropertyDetails::setAgreeToDecisionWithoutInspection
        );
        setIfNotNull(
            inspectionDetails.getNoDecisionWithoutInspectionReason(),
            casePropertyDetails::setNoDecisionWithoutInspectionReason
        );
        setIfNotNull(inspectionDetails.getHearingRequested(), casePropertyDetails::setHearingRequested);
        setIfNotNull(inspectionDetails.getReasonHearingRequested(), casePropertyDetails::setReasonHearingRequested);

        casePropertyDetailsRepository.save(casePropertyDetails);
    }

    @Transactional
    public void updateWithApplicationType(PTCaseEntity ptCaseEntity, ApplicationType applicationType) {
        CasePropertyDetailsEntity casePropertyDetails = findOrBuildCasePropertyDetails(ptCaseEntity);
        setIfNotNull(applicationType, casePropertyDetails::setMarketRentCaseType);

        casePropertyDetailsRepository.save(casePropertyDetails);
    }

    @Transactional
    public CasePropertyDetailsEntity getOrCreateCasePropertyDetails(PTCaseEntity ptCaseEntity) {
        CasePropertyDetailsEntity casePropertyDetails = findOrBuildCasePropertyDetails(ptCaseEntity);
        casePropertyDetailsRepository.save(casePropertyDetails);
        return casePropertyDetails;
    }

    private CasePropertyDetailsEntity findOrBuildCasePropertyDetails(PTCaseEntity ptCaseEntity) {
        CasePropertyDetailsEntity casePropertyDetails = ptCaseEntity.getCasePropertyDetails();
        if (casePropertyDetails == null) {
            casePropertyDetails = new CasePropertyDetailsEntity();
            ptCaseEntity.setCasePropertyDetails(casePropertyDetails);
        }
        casePropertyDetails.setPtCase(ptCaseEntity);
        return casePropertyDetails;
    }

    private BigDecimal toBigDecimal(Double value) {
        return value != null ? BigDecimal.valueOf(value) : null;
    }
}
