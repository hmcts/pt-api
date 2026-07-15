package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.pt.entity.CaseApplication;
import uk.gov.hmcts.reform.pt.entity.CasePartyAccess;
import uk.gov.hmcts.reform.pt.entity.CaseParty;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CaseApplicationRepositoryTest extends AbstractRepositoryTest<CaseApplicationRepository> {

    @Autowired
    protected CaseApplicationRepositoryTest(CaseApplicationRepository repository) {
        super(repository);
    }

    @Test
    void findAllByCasePartyAccessIdamId_returnsList() {
        UUID idamId = UUID.randomUUID();
        CaseApplication entity = createCaseApplication(1234567890123456L, idamId);
        repository.save(entity);

        List<CaseApplication> result = repository.findAllByCasePartyAccessIdamId(idamId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCaseParty().getAccess().getFirst().getIdamId()).isEqualTo(idamId);
    }

    @Test
    void findByPartyIdamIdAndCaseReference_returnsEntity_whenExists() {
        UUID idamId = UUID.randomUUID();
        long caseReference = 1234567890123456L;
        CaseApplication entity = createCaseApplication(caseReference, idamId);
        repository.save(entity);

        Optional<CaseApplication> result = repository.findByPartyIdamIdAndCaseReference(caseReference, idamId);

        assertThat(result).isPresent();
        assertThat(result.get().getCaseParty().getPtCase().getCaseReference()).isEqualTo(caseReference);
    }

    private CaseApplication createCaseApplication(long caseReference, UUID userId) {
        PTCaseEntity ptCase = PTCaseEntity.builder()
            .caseReference(caseReference)
            .build();

        CaseParty caseParty = CaseParty.builder()
            .ptCase(ptCase)
            .build();

        CasePartyAccess access = CasePartyAccess.builder()
            .idamId(userId)
            .party(caseParty)
            .build();

        caseParty.setAccess(List.of(access));

        return CaseApplication.builder()
            .caseParty(caseParty)
            .build();
    }
}
