package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.entity.StatementOfTruthEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class StatementOfTruthRepositoryTest extends AbstractRepositoryTest<StatementOfTruthRepository> {

    private final PTCaseRepository ptCaseRepository;

    @Autowired
    protected StatementOfTruthRepositoryTest(
        StatementOfTruthRepository repository,
        PTCaseRepository ptCaseRepository,
        CasePropertyDetailsRepository casePropertyDetailsRepository
    ) {
        super(repository);
        this.ptCaseRepository = ptCaseRepository;
    }

    @Test
    @DisplayName("Should save and retrieve application statement of truth entity")
    void saveAndFindApplicationStatementOfTruth() {
        long caseReference = 1234567890123456L;
        PTCaseEntity ptCase = ptCaseRepository.save(PTCaseEntity.builder().caseReference(caseReference).build());

        LocalDateTime completedDate = LocalDateTime.of(2026, 11, 5, 12, 0, 0);
        StatementOfTruthEntity statement = StatementOfTruthEntity.builder()
            .completedDate(completedDate)
            .completedBy("Applicant")
            .accepted(YesOrNo.YES)
            .fullName("Jane Doe")
            .firmName("Legal Firm Ltd")
            .positionHeld("Solicitor")
            .build();

        StatementOfTruthEntity saved = repository.save(statement);

        Optional<StatementOfTruthEntity> result = repository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isNotNull();
        assertThat(result.get().getCompletedDate()).isEqualTo(completedDate);
        assertThat(result.get().getCompletedBy()).isEqualTo("Applicant");
        assertThat(result.get().getAccepted()).isEqualTo(YesOrNo.YES);
        assertThat(result.get().getFullName()).isEqualTo("Jane Doe");
        assertThat(result.get().getFirmName()).isEqualTo("Legal Firm Ltd");
        assertThat(result.get().getPositionHeld()).isEqualTo("Solicitor");
    }
}
