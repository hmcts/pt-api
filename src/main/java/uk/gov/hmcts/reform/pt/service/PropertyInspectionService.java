package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.HearingPropertyInspectionDetails;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.PropertyInspectionEntity;
import uk.gov.hmcts.reform.pt.repository.PropertyInspectionRepository;

import static uk.gov.hmcts.reform.pt.util.NullSafeSetter.setIfNotNull;

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

        setIfNotNull(
            inspectionDetails.getAgreeToDecisionWithoutInspection(),
            entity::setAgreeToDecisionWithoutInspection
        );
        setIfNotNull(
            inspectionDetails.getNoDecisionWithoutInspectionReason(),
            entity::setNoDecisionWithoutInspectionReason
        );
        entity.setPtCase(ptCaseEntity);

        propertyInspectionRepository.save(entity);
    }
}
