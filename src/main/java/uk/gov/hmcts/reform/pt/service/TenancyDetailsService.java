package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.CurrentRentDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.TenancyAgreementDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.TenancyType;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.TenancyDetailsEntity;
import uk.gov.hmcts.reform.pt.repository.TenancyDetailsRepository;

import static uk.gov.hmcts.reform.pt.util.NullSafeSetter.setIfNotNull;

@Service
@RequiredArgsConstructor
public class TenancyDetailsService {

    private final TenancyDetailsRepository tenancyDetailsRepository;

    @Transactional
    public TenancyDetailsEntity getTenancyDetailsOrCreateIfNotExists(TenancyType tenancyType, PTCaseEntity ptCase) {
        return tenancyDetailsRepository.findFirstByTenancyTypeAndPtCase_CaseReference(
            tenancyType,
            ptCase.getCaseReference()
            ).orElseGet(() -> createTenancyDetails(tenancyType, ptCase));
    }

    @Transactional
    public TenancyDetailsEntity createTenancyDetails(TenancyType tenancyType, PTCaseEntity ptCase) {
        TenancyDetailsEntity tenancyDetails = TenancyDetailsEntity.builder()
            .tenancyType(tenancyType)
            .ptCase(ptCase)
            .build();
        return tenancyDetailsRepository.save(tenancyDetails);
    }

    @Transactional
    public void updateWithPropertyDetails(PTCaseEntity ptCaseEntity, PropertyDetails details) {
        TenancyDetailsEntity tenancyDetails = ptCaseEntity.getTenancyDetails().stream()
            .findFirst()
            // shouldn't ever reach orElse since there a pt case is created with a tenancy details entity
            .orElse(new TenancyDetailsEntity());

        tenancyDetails.setPtCase(ptCaseEntity);
        setIfNotNull(details.getOtherFacilitiesAvailable(), tenancyDetails::setTenancyIncludeFacilities);
        setIfNotNull(details.getOtherFacilitiesDetails(), tenancyDetails::setOtherFacilitiesDetails);
        setIfNotNull(details.getFurnitureProvidedInTenancy(), tenancyDetails::setFurnitureProvidedInTenancy);
        setIfNotNull(
            details.getFurnitureProvidedInTenancyDetails(),
            tenancyDetails::setFurnitureProvidedInTenancyDetails
        );
        setIfNotNull(
            details.getAdditionalServicesProvidedInTenancy(),
            tenancyDetails::setAdditionalServicesProvidedInTenancy
        );
        setIfNotNull(
            details.getAdditionalServicesProvidedInTenancyDetails(),
            tenancyDetails::setAdditionalServicesProvidedInTenancyDetails
        );
        setIfNotNull(details.getLandlordRepairsDetails(), tenancyDetails::setLandlordRepairsDetails);
        setIfNotNull(details.getTenantRepairsDetails(), tenancyDetails::setTenantRepairsDetails);
        setIfNotNull(details.getAnyTenantsMadePropertyRepairs(), tenancyDetails::setAnyTenantsMadePropertyRepairs);

        tenancyDetailsRepository.save(tenancyDetails);
    }

    @Transactional
    public void updateWithCurrentRentDetails(PTCaseEntity ptCaseEntity, CurrentRentDetails details) {
        TenancyDetailsEntity tenancyDetails = ptCaseEntity.getTenancyDetails().stream()
            .findFirst()
            // shouldn't ever reach orElse since there a pt case is created with a tenancy details entity
            .orElse(new TenancyDetailsEntity());

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
        setIfNotNull(
            details.getAdditionalRentalServiceChargesVary(),
            tenancyDetails::setAdditionalServicesProvidedInTenancy
        );
        setIfNotNull(
            details.getAdditionalRentalVaryingServiceChargesDetails(),
            tenancyDetails::setAdditionalServicesProvidedInTenancyDetails
        );

        tenancyDetailsRepository.save(tenancyDetails);
    }

    @Transactional
    public void updateWithTenancyAgreementDetails(PTCaseEntity ptCaseEntity, TenancyAgreementDetails details) {
        TenancyDetailsEntity tenancyDetails = ptCaseEntity.getTenancyDetails().stream()
            .findFirst()
            // shouldn't ever reach orElse since there a pt case is created with a tenancy details entity
            .orElse(new TenancyDetailsEntity());

        setIfNotNull(details.getCopyOfTenancyAgreement(), tenancyDetails::setCopyOfTenancyAgreement);
        setIfNotNull(details.getNoTenancyAgreementReason(), tenancyDetails::setNoTenancyAgreementReason);

        tenancyDetailsRepository.save(tenancyDetails);
    }
}
