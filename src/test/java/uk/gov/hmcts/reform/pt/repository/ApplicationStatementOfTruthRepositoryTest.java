package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.entity.ApplicationStatementOfTruthEntity;
import uk.gov.hmcts.reform.pt.entity.MarketRentCaseEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationStatementOfTruthRepositoryTest extends AbstractRepositoryTest<ApplicationStatementOfTruthRepository> {

    private final PTCaseRepository ptCaseRepository;
    private final MarketRentCaseRepository marketRentCaseRepository;

    @Autowired
    protected ApplicationStatementOfTruthRepositoryTest(
        ApplicationStatementOfTruthRepository repository,
        PTCaseRepository ptCaseRepository,
        MarketRentCaseRepository marketRentCaseRepository
    ) {
        super(repository);
        this.ptCaseRepository = ptCaseRepository;
        this.marketRentCaseRepository = marketRentCaseRepository;
    }

    @Test
    @DisplayName("Should save and retrieve application statement of truth entity")
    void saveAndFindApplicationStatementOfTruth() {
        long caseReference = 1234567890123456L;
        PTCaseEntity ptCase = ptCaseRepository.save(PTCaseEntity.builder().caseReference(caseReference).build());
        MarketRentCaseEntity marketRentCase = marketRentCaseRepository.save(
            MarketRentCaseEntity.builder().ptCase(ptCase).build());

        LocalDateTime completedDate = LocalDateTime.of(2026, 11, 5, 12, 0, 0);
        ApplicationStatementOfTruthEntity statement = ApplicationStatementOfTruthEntity.builder()
            .completedDate(completedDate)
            .completedBy("Applicant")
            .accepted(YesOrNo.YES)
            .fullName("Jane Doe")
            .firmName("Legal Firm Ltd")
            .positionHeld("Solicitor")
            .marketRentCase(marketRentCase)
            .build();

        ApplicationStatementOfTruthEntity saved = repository.save(statement);

        Optional<ApplicationStatementOfTruthEntity> result = repository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isNotNull();
        assertThat(result.get().getCompletedDate()).isEqualTo(completedDate);
        assertThat(result.get().getCompletedBy()).isEqualTo("Applicant");
        assertThat(result.get().getAccepted()).isEqualTo(YesOrNo.YES);
        assertThat(result.get().getFullName()).isEqualTo("Jane Doe");
        assertThat(result.get().getFirmName()).isEqualTo("Legal Firm Ltd");
        assertThat(result.get().getPositionHeld()).isEqualTo("Solicitor");
        assertThat(result.get().getMarketRentCase().getId()).isEqualTo(marketRentCase.getId());
        assertThat(result.get().getMarketRentCase().getPtCase().getCaseReference()).isEqualTo(caseReference);
    }
}
