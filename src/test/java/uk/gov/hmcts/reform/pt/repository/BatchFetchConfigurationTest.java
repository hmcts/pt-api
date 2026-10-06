package uk.gov.hmcts.reform.pt.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.pt.entity.CaseApplicationEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyAccessEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BatchFetchConfigurationTest extends AbstractRepositoryTest<CaseApplicationRepository> {

    private static final int APPLICATION_COUNT = 3;

    @PersistenceContext
    private EntityManager entityManager;

    private final PTCaseRepository ptCaseRepository;
    private final CasePartyRepository casePartyRepository;

    @Autowired
    protected BatchFetchConfigurationTest(
        CaseApplicationRepository repository,
        PTCaseRepository ptCaseRepository,
        CasePartyRepository casePartyRepository
    ) {
        super(repository);
        this.ptCaseRepository = ptCaseRepository;
        this.casePartyRepository = casePartyRepository;
    }

    @Test
    void lazyAssociationsAreLoadedInBatchesRatherThanOnePerOwner() {
        UUID idamId = UUID.randomUUID();
        for (int i = 0; i < APPLICATION_COUNT; i++) {
            saveApplication(1234567890123450L + i, idamId);
        }
        entityManager.flush();
        entityManager.clear();

        List<CaseApplicationEntity> applications = repository.findAllByCasePartyAccessIdamId(idamId);
        assertThat(applications).hasSize(APPLICATION_COUNT);

        Statistics statistics = entityManager.getEntityManagerFactory()
            .unwrap(SessionFactory.class)
            .getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();

        applications.forEach(application -> application.getCaseParty().getPtCase().getCaseReference());

        assertThat(statistics.getPrepareStatementCount())
            .isEqualTo(2); // would be 6 without batching
    }

    private void saveApplication(long caseReference, UUID idamId) {
        PTCaseEntity ptCase = PTCaseEntity.builder().caseReference(caseReference).build();
        CasePartyEntity caseParty = CasePartyEntity.builder().ptCase(ptCase).build();
        caseParty.setAccess(List.of(
            CasePartyAccessEntity.builder().idamId(idamId).party(caseParty).build()));
        CaseApplicationEntity caseApplication = CaseApplicationEntity.builder().caseParty(caseParty).build();

        ptCaseRepository.save(ptCase);
        casePartyRepository.save(caseParty);
        repository.save(caseApplication);
    }
}
