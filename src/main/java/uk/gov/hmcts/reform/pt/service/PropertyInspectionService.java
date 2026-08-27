package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.HearingPropertyInspectionDetails;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.PropertyInspectionEntity;
import uk.gov.hmcts.reform.pt.repository.PropertyInspectionRepository;

@Service
@RequiredArgsConstructor
public class PropertyInspectionService {

    private final PropertyInspectionRepository propertyInspectionRepository;

    @Transactional
    public void updatePropertyInspection(
        PTCaseEntity ptCaseEntity, HearingPropertyInspectionDetails inspectionDetails
    ) {
        PropertyInspectionEntity entity = ptCaseEntity.getPropertyInspections().stream()
            .findFirst()
            .orElse(new PropertyInspectionEntity());

        entity.setAgreeToDecisionWithoutInspection(inspectionDetails.getAgreeToDecisionWithoutInspection());
        entity.setNoDecisionWithoutInspectionReason(inspectionDetails.getNoDecisionWithoutInspectionReason());
        entity.setPtCase(ptCaseEntity);
        propertyInspectionRepository.save(entity);
    }
}
