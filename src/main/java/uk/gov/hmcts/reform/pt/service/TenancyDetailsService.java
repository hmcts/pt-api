package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.ccd.domain.RentDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.TenancyAgreementDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.TenancyType;
import uk.gov.hmcts.reform.pt.entity.CasePropertyDetailsEntity;
import uk.gov.hmcts.reform.pt.ccd.domain.YesNoNotSure;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.TenancyDetailsEntity;
import uk.gov.hmcts.reform.pt.repository.TenancyDetailsRepository;

import static uk.gov.hmcts.reform.pt.util.NullSafeSetter.setIfElseNull;
import static uk.gov.hmcts.reform.pt.util.NullSafeSetter.setIfNotNull;

@Service
@RequiredArgsConstructor
public class TenancyDetailsService {

    private final TenancyDetailsRepository tenancyDetailsRepository;
    private final CasePropertyDetailsService casePropertyDetailsService;

    @Transactional
    public TenancyDetailsEntity getTenancyDetailsOrCreateIfNotExists(TenancyType tenancyType, PTCaseEntity ptCase) {
        return tenancyDetailsRepository.findFirstByTenancyTypeAndCasePropertyDetails_PtCase_CaseReference(
            tenancyType,
            ptCase.getCaseReference()
            ).orElseGet(() -> createTenancyDetails(tenancyType, ptCase));
    }

    @Transactional
    public TenancyDetailsEntity createTenancyDetails(TenancyType tenancyType, PTCaseEntity ptCase) {
        CasePropertyDetailsEntity casePropertyDetails =
            casePropertyDetailsService.getOrCreateCasePropertyDetails(ptCase);
        TenancyDetailsEntity tenancyDetails = TenancyDetailsEntity.builder()
            .tenancyType(tenancyType)
            .casePropertyDetails(casePropertyDetails)
            .build();
        casePropertyDetails.setTenancyDetails(tenancyDetails);
        return tenancyDetailsRepository.save(tenancyDetails);
    }

    @Transactional
    public void updateWithPropertyDetails(PTCaseEntity ptCaseEntity, PropertyDetails details) {
        TenancyDetailsEntity tenancyDetails = getOrCreateTenancyDetails(ptCaseEntity);
        setIfNotNull(details.getOtherFacilitiesAvailable(), tenancyDetails::setTenancyIncludeFacilities);
        setIfElseNull(
            () -> details.getOtherFacilitiesAvailable() == YesOrNo.YES,
            details::getOtherFacilitiesDetails,
            tenancyDetails::setOtherFacilitiesDetails
        );
        setIfNotNull(details.getFurnitureProvidedInTenancy(), tenancyDetails::setFurnitureProvidedInTenancy);
        setIfElseNull(
            () -> details.getFurnitureProvidedInTenancy() == YesOrNo.YES,
            details::getFurnitureProvidedInTenancyDetails,
            tenancyDetails::setFurnitureProvidedInTenancyDetails
        );
        setIfNotNull(
            details.getAdditionalServicesProvidedInTenancy(),
            tenancyDetails::setAdditionalServicesProvidedInTenancy
        );
        setIfElseNull(
            () -> details.getAdditionalServicesProvidedInTenancy() == YesOrNo.YES,
            details::getAdditionalServicesProvidedInTenancyDetails,
            tenancyDetails::setAdditionalServicesProvidedInTenancyDetails
        );
        setIfNotNull(details.getLandlordRepairsDetails(), tenancyDetails::setLandlordRepairsDetails);
        setIfNotNull(details.getTenantRepairsDetails(), tenancyDetails::setTenantRepairsDetails);
        setIfNotNull(details.getAnyTenantsMadePropertyRepairs(), tenancyDetails::setAnyTenantsMadePropertyRepairs);
        setIfElseNull(() -> details.getAnyTenantsMadePropertyRepairs() == YesNoNotSure.YES,
                      details::getTenantsPropertyRepairsDetails,
                      tenancyDetails::setTenantsPropertyRepairsDetails);

        tenancyDetailsRepository.save(tenancyDetails);
    }

    @Transactional
    public void updateWithRentDetails(PTCaseEntity ptCaseEntity, RentDetails details) {
        TenancyDetailsEntity tenancyDetails = getOrCreateTenancyDetails(ptCaseEntity);
        setIfNotNull(
            details.getTribunalPreviouslyDeterminedTenancyRent(),
            tenancyDetails::setTribunalPreviouslyDeterminedTenancyRent
        );
        setIfNotNull(details.getPreviousTribunalCaseReference(), tenancyDetails::setPreviousTribunalCaseReference);
        setIfNotNull(details.getCurrentTenancyStartDate(), tenancyDetails::setCurrentTenancyStartDate);
        setIfNotNull(details.getCurrentTenancyEndDate(), tenancyDetails::setTenancyEndDate);
        setIfNotNull(
            details.getCurrentTenancyReplaceOriginalTenancy(),
            tenancyDetails::setCurrentTenancyReplaceOriginalTenancy
        );
        setIfNotNull(details.getOriginalTenancyStartDate(), tenancyDetails::setOriginalTenancyStartDate);

        tenancyDetailsRepository.save(tenancyDetails);
    }

    @Transactional
    public void updateWithTenancyAgreementDetails(PTCaseEntity ptCaseEntity, TenancyAgreementDetails details) {
        TenancyDetailsEntity tenancyDetails = getOrCreateTenancyDetails(ptCaseEntity);
        setIfNotNull(details.getCopyOfTenancyAgreement(), tenancyDetails::setCopyOfTenancyAgreement);
        setIfNotNull(details.getNoTenancyAgreementReason(), tenancyDetails::setNoTenancyAgreementReason);

        tenancyDetailsRepository.save(tenancyDetails);
    }

    private TenancyDetailsEntity getOrCreateTenancyDetails(PTCaseEntity ptCaseEntity) {
        CasePropertyDetailsEntity casePropertyDetails =
            casePropertyDetailsService.getOrCreateCasePropertyDetails(ptCaseEntity);
        TenancyDetailsEntity tenancyDetails = casePropertyDetails.getTenancyDetails();
        if (tenancyDetails == null) {
            tenancyDetails = TenancyDetailsEntity.builder()
                .casePropertyDetails(casePropertyDetails)
                .build();
            casePropertyDetails.setTenancyDetails(tenancyDetails);
        }
        return tenancyDetails;
    }
}
