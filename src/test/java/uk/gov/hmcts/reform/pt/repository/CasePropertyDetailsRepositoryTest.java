package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.ccd.domain.Frequency;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyType;
import uk.gov.hmcts.reform.pt.entity.CasePropertyDetailsEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CasePropertyDetailsRepositoryTest extends AbstractRepositoryTest<CasePropertyDetailsRepository> {

    private final PTCaseRepository ptCaseRepository;

    @Autowired
    protected CasePropertyDetailsRepositoryTest(
        CasePropertyDetailsRepository repository,
        PTCaseRepository ptCaseRepository
    ) {
        super(repository);
        this.ptCaseRepository = ptCaseRepository;
    }

    @Test
    @DisplayName("Should save and retrieve market rent case entity")
    void saveAndFindMarketRentCase() {
        long caseReference = 1234567890123456L;
        PTCaseEntity ptCase = ptCaseRepository.save(PTCaseEntity.builder().caseReference(caseReference).build());

        CasePropertyDetailsEntity marketRentCase = CasePropertyDetailsEntity.builder()
            .method("Online")
            .channel("Citizen")
            .status("In Progress")
            .propertyFloorPlanAvailable(YesOrNo.YES)
            .typeOfPropertyRenting(PropertyType.FLAT)
            .rentPaymentFrequency(Frequency.MONTHLY)
            .rentCostMonthly(new BigDecimal("1200.00"))
            .applicantSuggestedMarketRent(new BigDecimal("1100.00"))
            .applicantSuggestedMarketRentFrequency(Frequency.MONTHLY)
            // app type move
            //            .marketRentCaseType(ApplicationType.CHALLENGE_RENT_INCREASE)
            .language("en")
            .ptCase(ptCase)
            .build();

        CasePropertyDetailsEntity saved = repository.save(marketRentCase);

        Optional<CasePropertyDetailsEntity> result = repository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isNotNull();
        assertThat(result.get().getMethod()).isEqualTo("Online");
        assertThat(result.get().getChannel()).isEqualTo("Citizen");
        assertThat(result.get().getStatus()).isEqualTo("In Progress");
        assertThat(result.get().getPropertyFloorPlanAvailable()).isEqualTo(YesOrNo.YES);
        assertThat(result.get().getTypeOfPropertyRenting()).isEqualTo(PropertyType.FLAT);
        assertThat(result.get().getRentPaymentFrequency()).isEqualTo(Frequency.MONTHLY);
        assertThat(result.get().getRentCostMonthly()).isEqualByComparingTo("1200.00");
        assertThat(result.get().getApplicantSuggestedMarketRent()).isEqualByComparingTo("1100.00");
        // app type move
        //       assertThat(result.get().getMarketRentCaseType()).isEqualTo(ApplicationType.CHALLENGE_RENT_INCREASE);
        assertThat(result.get().getLanguage()).isEqualTo("en");
        assertThat(result.get().getPtCase().getId()).isEqualTo(ptCase.getId());
        assertThat(result.get().getPtCase().getCaseReference()).isEqualTo(caseReference);
    }
}
