package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.pt.entity.CaseParty;
import uk.gov.hmcts.reform.pt.entity.CasePartyAccess;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CasePartyRepositoryTest extends AbstractRepositoryTest<CasePartyRepository> {

    @Autowired
    protected CasePartyRepositoryTest(CasePartyRepository repository) {
        super(repository);
    }

    @Test
    @DisplayName("Should return case party associated with access idam id")
    void findFirstByAccessIdamIdReturnsCaseParty() {
        UUID idamId = UUID.randomUUID();
        CaseParty party = CaseParty.builder()
            .firstName("FirstName")
            .lastName("LastName")
            .build();

        CasePartyAccess access = CasePartyAccess.builder()
            .idamId(idamId)
            .party(party)
            .build();

        party.setAccess(List.of(access));
        repository.save(party);

        Optional<CaseParty> result = repository.findFirstByAccessIdamId(idamId);

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("FirstName");
    }
}
