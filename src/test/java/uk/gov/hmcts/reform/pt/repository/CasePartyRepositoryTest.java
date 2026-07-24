package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyAccessEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CasePartyRepositoryTest extends AbstractRepositoryTest<CasePartyRepository> {

    private final PTCaseRepository ptCaseRepository;

    @Autowired
    protected CasePartyRepositoryTest(CasePartyRepository repository, PTCaseRepository ptCaseRepository) {
        super(repository);
        this.ptCaseRepository = ptCaseRepository;
    }

    @Test
    @DisplayName("Should return case party associated with access idam id")
    void findFirstByAccessIdamIdReturnsCaseParty() {
        UUID idamId = UUID.randomUUID();
        CasePartyEntity party = CasePartyEntity.builder()
            .firstName("FirstName")
            .lastName("LastName")
            .build();

        CasePartyAccessEntity access = CasePartyAccessEntity.builder()
            .idamId(idamId)
            .party(party)
            .build();

        party.setAccess(List.of(access));
        repository.save(party);

        Optional<CasePartyEntity> result = repository.findFirstByAccessIdamId(idamId);

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("FirstName");
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
}
