package uk.gov.hmcts.reform.pt.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.pt.entity.CaseApplicationEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyAccessEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.projection.ApplicationSummary;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CaseApplicationRepositoryTest extends AbstractRepositoryTest<CaseApplicationRepository> {

    @PersistenceContext
    private EntityManager entityManager;

    private final PTCaseRepository ptCaseRepository;
    private final CasePartyRepository casePartyRepository;

    @Autowired
    protected CaseApplicationRepositoryTest(
        CaseApplicationRepository repository,
        PTCaseRepository ptCaseRepository,
        CasePartyRepository casePartyRepository
    ) {
        super(repository);
        this.ptCaseRepository = ptCaseRepository;
        this.casePartyRepository = casePartyRepository;
    }

    @Test
    void findAllByCasePartyAccessIdamId_returnsList() {
        UUID idamId = UUID.randomUUID();
        saveApplication(1234567890123456L, idamId);

        List<CaseApplicationEntity> result = repository.findAllByCasePartyAccessIdamId(idamId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCaseParty().getAccess().getFirst().getIdamId()).isEqualTo(idamId);
    }

    @Test
    void findByPartyIdamIdAndCaseReference_returnsEntity_whenExists() {
        UUID idamId = UUID.randomUUID();
        long caseReference = 1234567890123456L;

        saveApplication(caseReference, idamId);

        Optional<CaseApplicationEntity> result = repository.findByPartyIdamIdAndCaseReference(caseReference, idamId);

        assertThat(result).isPresent();
        assertThat(result.get().getCaseParty().getPtCase().getCaseReference()).isEqualTo(caseReference);
    }

    @Test
    void findActiveByCasePartyAccessIdamId_bindsProjectionAndExcludesPendingDisposal() {
        UUID idamId = UUID.randomUUID();
        long activeReference = 1234567890123456L;
        long disposingReference = 9876543210987654L;

        saveApplication(activeReference, idamId);
        saveApplication(disposingReference, idamId);

        insertCcdCase(activeReference, "AWAITING_SUBMISSION_TO_HMCTS");
        insertCcdCase(disposingReference, "PendingDisposal");

        List<ApplicationSummary> result = repository.findActiveByCasePartyAccessIdamId(idamId);

        assertThat(result).hasSize(1);
        ApplicationSummary summary = result.getFirst();
        assertThat(summary.getCaseReference()).isEqualTo(activeReference);
        assertThat(summary.getId()).isNotNull();
        assertThat(summary.getCreatedDate()).isNotNull();
    }

    private void saveApplication(long caseReference, UUID idamId) {
        CaseApplicationEntity entity = createCaseApplication(caseReference, idamId);
        ptCaseRepository.save(entity.getCaseParty().getPtCase());
        casePartyRepository.save(entity.getCaseParty());
        repository.save(entity);
    }

    private void insertCcdCase(long reference, String state) {
        entityManager.createNativeQuery(
            "INSERT INTO ccd.case_data "
                + "(reference, id, version, case_revision, security_classification, "
                + " jurisdiction, case_type_id, state, data) "
                + "VALUES (?1, ?1, 1, 1, 'PUBLIC'::ccd.securityclassification, "
                + " 'PT', 'PT', ?2, '{}'::jsonb)"
        )
            .setParameter(1, reference)
            .setParameter(2, state)
            .executeUpdate();
        entityManager.flush();
    }

    private CaseApplicationEntity createCaseApplication(long caseReference, UUID userId) {
        PTCaseEntity ptCase = PTCaseEntity.builder()
            .caseReference(caseReference)
            .build();

        CasePartyEntity caseParty = CasePartyEntity.builder()
            .ptCase(ptCase)
            .build();

        CasePartyAccessEntity access = CasePartyAccessEntity.builder()
            .idamId(userId)
            .party(caseParty)
            .build();

        caseParty.setAccess(List.of(access));

        return CaseApplicationEntity.builder()
            .caseParty(caseParty)
            .build();
    }
}
