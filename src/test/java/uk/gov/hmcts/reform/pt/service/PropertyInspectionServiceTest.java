package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.ccd.domain.HearingPropertyInspectionDetails;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.PropertyInspectionEntity;
import uk.gov.hmcts.reform.pt.repository.PropertyInspectionRepository;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PropertyInspectionServiceTest {

    @Mock
    private PropertyInspectionRepository propertyInspectionRepository;

    @InjectMocks
    private PropertyInspectionService propertyInspectionService;

    @Test
    @DisplayName("Should create new property inspection when none exist")
    void updatePropertyInspectionCreatesNew() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .propertyInspections(Collections.emptyList())
            .build();
        HearingPropertyInspectionDetails inspectionDetails = HearingPropertyInspectionDetails.builder()
            .agreeToDecisionWithoutInspection(YesOrNo.YES)
            .noDecisionWithoutInspectionReason("No inspection required")
            .build();

        propertyInspectionService.updatePropertyInspection(ptCaseEntity, inspectionDetails);

        ArgumentCaptor<PropertyInspectionEntity> captor =
            ArgumentCaptor.forClass(PropertyInspectionEntity.class);
        verify(propertyInspectionRepository).save(captor.capture());
        PropertyInspectionEntity saved = captor.getValue();

        assertThat(saved.getPtCase()).isEqualTo(ptCaseEntity);
        assertThat(saved.getAgreeToDecisionWithoutInspection()).isEqualTo(YesOrNo.YES);
        assertThat(saved.getNoDecisionWithoutInspectionReason()).isEqualTo("No inspection required");
    }

    @Test
    @DisplayName("Should update existing property inspection when it exists")
    void updatePropertyInspectionUpdatesExisting() {
        PropertyInspectionEntity existing = PropertyInspectionEntity.builder()
            .agreeToDecisionWithoutInspection(YesOrNo.NO)
            .noDecisionWithoutInspectionReason("Old reason")
            .build();
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .propertyInspections(List.of(existing))
            .build();
        HearingPropertyInspectionDetails inspectionDetails = HearingPropertyInspectionDetails.builder()
            .agreeToDecisionWithoutInspection(YesOrNo.YES)
            .noDecisionWithoutInspectionReason("Updated reason")
            .build();

        propertyInspectionService.updatePropertyInspection(ptCaseEntity, inspectionDetails);

        verify(propertyInspectionRepository).save(existing);
        assertThat(existing.getAgreeToDecisionWithoutInspection()).isEqualTo(YesOrNo.YES);
        assertThat(existing.getNoDecisionWithoutInspectionReason()).isEqualTo("Updated reason");
        assertThat(existing.getPtCase()).isEqualTo(ptCaseEntity);
    }
}
