package uk.gov.hmcts.reform.pt.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.MarketRentCaseEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.projection.ApplicationSummary;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CasePartyRepositoryTest extends AbstractRepositoryTest<CasePartyRepository> {

    @PersistenceContext
    private EntityManager entityManager;

    private final PTCaseRepository ptCaseRepository;
    private final MarketRentCaseRepository marketRentCaseRepository;

    @Autowired
    protected CasePartyRepositoryTest(
        CasePartyRepository repository,
        PTCaseRepository ptCaseRepository,
        MarketRentCaseRepository marketRentCaseRepository
    ) {
        super(repository);
        this.ptCaseRepository = ptCaseRepository;
        this.marketRentCaseRepository = marketRentCaseRepository;
    }

    @Test
    @DisplayName("Should return case party associated with a case reference")
    void findFirstByPtCaseCaseReferenceReturnsCaseParty() {
        long caseReference = 1234567890123456L;
        PTCaseEntity ptCase = PTCaseEntity.builder()
            .caseReference(caseReference)
            .build();
        ptCaseRepository.save(ptCase);

        CasePartyEntity party = CasePartyEntity.builder()
            .firstName("FirstName")
            .ptCase(ptCase)
            .build();
        repository.save(party);

        Optional<CasePartyEntity> result = repository.findFirstByPtCaseCaseReference(caseReference);

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("FirstName");
    }

    @Test
    void findAllByIdamId_returnsList() {
        UUID idamId = UUID.randomUUID();
        saveCaseParty(1234567890123456L, idamId);

        List<CasePartyEntity> result = repository.findAllByIdamId(idamId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getIdamId()).isEqualTo(idamId);
    }

    @Test
    void findByIdamIdAndCaseReference_returnsEntity_whenExists() {
        UUID idamId = UUID.randomUUID();
        long caseReference = 1234567890123456L;

        saveCaseParty(caseReference, idamId);

        insertCcdCase(caseReference, "AWAITING_SUBMISSION_TO_HMCTS");

        Optional<CasePartyEntity> result = repository.findByIdamIdAndCaseReference(caseReference, idamId);

        assertThat(result).isPresent();
        assertThat(result.get().getPtCase().getCaseReference()).isEqualTo(caseReference);
    }

    @Test
    void findActiveApplicationsByIdamId_bindsProjectionAndExcludesPendingDisposal() {
        UUID idamId = UUID.randomUUID();
        long activeReference = 1234567890123456L;
        long disposingReference = 9876543210987654L;
        LocalDateTime submittedDate = LocalDateTime.of(2026, 3, 1, 10, 0);

        PTCaseEntity activeCase = saveCaseParty(activeReference, idamId);
        saveCaseParty(disposingReference, idamId);
        marketRentCaseRepository.save(MarketRentCaseEntity.builder()
            .ptCase(activeCase)
            .submittedDate(submittedDate)
            .build());

        insertCcdCase(activeReference, "AWAITING_SUBMISSION_TO_HMCTS");
        insertCcdCase(disposingReference, "PendingDisposal");

        List<ApplicationSummary> result = repository.findActiveApplicationsByIdamId(idamId);

        assertThat(result).hasSize(1);
        ApplicationSummary summary = result.getFirst();
        assertThat(summary.getCaseReference()).isEqualTo(activeReference);
        assertThat(summary.getId()).isNotNull();
        assertThat(summary.getCreatedDate()).isNotNull();
        assertThat(summary.getSubmittedDate()).isEqualTo(submittedDate);
    }

    @Test
    void findByIdamIdAndCaseReference_returnsEmpty_whenPendingDisposal() {
        UUID idamId = UUID.randomUUID();
        long caseReference = 1234567890123456L;
        saveCaseParty(caseReference, idamId);
        insertCcdCase(caseReference, "PendingDisposal");

        Optional<CasePartyEntity> result = repository.findByIdamIdAndCaseReference(caseReference, idamId);

        assertThat(result).isEmpty();
    }

    private PTCaseEntity saveCaseParty(long caseReference, UUID idamId) {
        PTCaseEntity ptCase = ptCaseRepository.save(PTCaseEntity.builder()
            .caseReference(caseReference)
            .build());
        repository.save(CasePartyEntity.builder()
            .ptCase(ptCase)
            .idamId(idamId)
            .build());
        return ptCase;
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
}
