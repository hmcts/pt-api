package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.TenancyType;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.TenancyDetailsEntity;
import uk.gov.hmcts.reform.pt.repository.TenancyDetailsRepository;

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
        tenancyDetails.setTenancyIncludeFacilities(details.getOtherFacilitiesAvailable());
        tenancyDetails.setOtherFacilitiesDetails(details.getOtherFacilitiesDetails());
        tenancyDetails.setFurnitureProvidedInTenancy(details.getFurnitureProvidedInTenancy());
        tenancyDetails.setFurnitureProvidedInTenancyDetails(details.getFurnitureProvidedInTenancyDetails());
        tenancyDetails.setAdditionalServicesProvidedInTenancy(details.getAdditionalServicesProvidedInTenancy());
        tenancyDetails.setAdditionalServicesProvidedInTenancyDetails(
            details.getAdditionalServicesProvidedInTenancyDetails());
        tenancyDetails.setLandlordRepairsDetails(details.getLandlordRepairsDetails());
        tenancyDetails.setTenantRepairsDetails(details.getTenantRepairsDetails());
        tenancyDetails.setAnyTenantsMadePropertyRepairs(details.getAnyTenantsMadePropertyRepairs());

        tenancyDetailsRepository.save(tenancyDetails);
    }
}
