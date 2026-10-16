package uk.gov.hmcts.reform.pt.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BatchFetchConfigurationTest extends AbstractRepositoryTest<CasePartyRepository> {

    private static final int CASE_COUNT = 3;

    @PersistenceContext
    private EntityManager entityManager;

    private final PTCaseRepository ptCaseRepository;

    @Autowired
    protected BatchFetchConfigurationTest(
        CasePartyRepository repository,
        PTCaseRepository ptCaseRepository
    ) {
        super(repository);
        this.ptCaseRepository = ptCaseRepository;
    }

    @Test
    void lazyAssociationsAreLoadedInBatchesRatherThanOnePerOwner() {
        UUID idamId = UUID.randomUUID();
        for (int i = 0; i < CASE_COUNT; i++) {
            saveCaseParty(1234567890123450L + i, idamId);
        }
        entityManager.flush();
        entityManager.clear();

        List<CasePartyEntity> caseParties = repository.findAllByIdamId(idamId);
        assertThat(caseParties).hasSize(CASE_COUNT);

        Statistics statistics = entityManager.getEntityManagerFactory()
            .unwrap(SessionFactory.class)
            .getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();

        caseParties.forEach(caseParty -> caseParty.getPtCase().getCaseReference());

        assertThat(statistics.getPrepareStatementCount())
            .isEqualTo(1); // would be 3 without batching
    }

    private void saveCaseParty(long caseReference, UUID idamId) {
        PTCaseEntity ptCase = PTCaseEntity.builder().caseReference(caseReference).build();
        CasePartyEntity caseParty = CasePartyEntity.builder().ptCase(ptCase).idamId(idamId).build();

        ptCaseRepository.save(ptCase);
        repository.save(caseParty);
    }
}
