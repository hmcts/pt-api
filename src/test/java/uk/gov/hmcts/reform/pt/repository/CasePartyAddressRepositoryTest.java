package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.pt.entity.CasePartyAddressEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CasePartyAddressRepositoryTest extends AbstractRepositoryTest<CasePartyAddressRepository> {

    private final PTCaseRepository ptCaseRepository;
    private final CasePartyRepository casePartyRepository;

    @Autowired
    protected CasePartyAddressRepositoryTest(
        CasePartyAddressRepository repository,
        PTCaseRepository ptCaseRepository,
        CasePartyRepository casePartyRepository
    ) {
        super(repository);
        this.ptCaseRepository = ptCaseRepository;
        this.casePartyRepository = casePartyRepository;
    }

    @Test
    @DisplayName("Should save and retrieve case party address entity")
    void saveAndFindCasePartyAddress() {
        long caseReference = 1234567890123456L;
        PTCaseEntity ptCase = ptCaseRepository.save(PTCaseEntity.builder().caseReference(caseReference).build());
        CasePartyEntity party = casePartyRepository.save(
            CasePartyEntity.builder()
                .ptCase(ptCase)
                .firstName("John")
                .lastName("Smith")
                .build());

        CasePartyAddressEntity address = CasePartyAddressEntity.builder()
            .addressLine1("10 High Street")
            .addressLine2("Flat 2")
            .townCity("London")
            .county("Greater London")
            .postcode("SW1A 1AA")
            .party(party)
            .build();

        CasePartyAddressEntity saved = repository.save(address);

        Optional<CasePartyAddressEntity> result = repository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isNotNull();
        assertThat(result.get().getAddressLine1()).isEqualTo("10 High Street");
        assertThat(result.get().getAddressLine2()).isEqualTo("Flat 2");
        assertThat(result.get().getTownCity()).isEqualTo("London");
        assertThat(result.get().getCounty()).isEqualTo("Greater London");
        assertThat(result.get().getPostcode()).isEqualTo("SW1A 1AA");
        assertThat(result.get().getParty().getId()).isEqualTo(party.getId());
    }
}
