package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.pt.entity.CaseApplicationEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyAccessEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CaseApplicationRepositoryTest extends AbstractRepositoryTest<CaseApplicationRepository> {

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
        CaseApplicationEntity entity = createCaseApplication(1234567890123456L, idamId);
        ptCaseRepository.save(entity.getCaseParty().getPtCase());
        casePartyRepository.save(entity.getCaseParty());
        repository.save(entity);

        List<CaseApplicationEntity> result = repository.findAllByCasePartyAccessIdamId(idamId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCaseParty().getAccess().getFirst().getIdamId()).isEqualTo(idamId);
    }

    @Test
    void findByPartyIdamIdAndCaseReference_returnsEntity_whenExists() {
        UUID idamId = UUID.randomUUID();
        long caseReference = 1234567890123456L;
        CaseApplicationEntity entity = createCaseApplication(caseReference, idamId);
        ptCaseRepository.save(entity.getCaseParty().getPtCase());
        casePartyRepository.save(entity.getCaseParty());
        repository.save(entity);

        Optional<CaseApplicationEntity> result = repository.findByPartyIdamIdAndCaseReference(caseReference, idamId);

        assertThat(result).isPresent();
        assertThat(result.get().getCaseParty().getPtCase().getCaseReference()).isEqualTo(caseReference);
    }

    @Test
    void findFirstByCasePartyId_returnsEntity_whenExists() {
        UUID idamId = UUID.randomUUID();
        CaseApplicationEntity entity = createCaseApplication(1234567890123456L, idamId);
        ptCaseRepository.save(entity.getCaseParty().getPtCase());
        casePartyRepository.save(entity.getCaseParty());
        repository.save(entity);

        Optional<CaseApplicationEntity> result =
            repository.findFirstByCasePartyId(entity.getCaseParty().getId());

        assertThat(result).isPresent();
        assertThat(result.get().getCaseParty().getId()).isEqualTo(entity.getCaseParty().getId());
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
