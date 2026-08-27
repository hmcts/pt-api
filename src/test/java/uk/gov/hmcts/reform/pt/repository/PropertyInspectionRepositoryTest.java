package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.PropertyInspectionEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyInspectionRepositoryTest extends AbstractRepositoryTest<PropertyInspectionRepository> {

    private final PTCaseRepository ptCaseRepository;

    @Autowired
    protected PropertyInspectionRepositoryTest(
        PropertyInspectionRepository repository,
        PTCaseRepository ptCaseRepository
    ) {
        super(repository);
        this.ptCaseRepository = ptCaseRepository;
    }

    @Test
    @DisplayName("Should save and retrieve property inspection entity")
    void saveAndFindPropertyInspection() {
        long caseReference = 1234567890123456L;
        PTCaseEntity ptCase = PTCaseEntity.builder()
            .caseReference(caseReference)
            .hearingRequested(YesOrNo.YES)
            .build();
        ptCaseRepository.save(ptCase);

        PropertyInspectionEntity propertyInspection = PropertyInspectionEntity.builder()
            .agreeToDecisionWithoutInspection(YesOrNo.NO)
            .noDecisionWithoutInspectionReason("Need inspection")
            .ptCase(ptCase)
            .build();
        PropertyInspectionEntity saved = repository.save(propertyInspection);

        Optional<PropertyInspectionEntity> result = repository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getAgreeToDecisionWithoutInspection()).isEqualTo(YesOrNo.NO);
        assertThat(result.get().getNoDecisionWithoutInspectionReason()).isEqualTo("Need inspection");
        assertThat(result.get().getPtCase().getCaseReference()).isEqualTo(caseReference);
    }
}
