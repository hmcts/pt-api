package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.pt.entity.CasePropertyAddressEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CasePropertyAddressRepositoryTest extends AbstractRepositoryTest<CasePropertyAddressRepository> {

    private final PTCaseRepository ptCaseRepository;

    @Autowired
    protected CasePropertyAddressRepositoryTest(
        CasePropertyAddressRepository repository,
        PTCaseRepository ptCaseRepository
    ) {
        super(repository);
        this.ptCaseRepository = ptCaseRepository;
    }

    @Test
    @DisplayName("Should save and retrieve case property address entity")
    void saveAndFindCasePropertyAddress() {
        long caseReference = 1234567890123456L;
        PTCaseEntity ptCase = ptCaseRepository.save(PTCaseEntity.builder().caseReference(caseReference).build());

        CasePropertyAddressEntity address = CasePropertyAddressEntity.builder()
            .addressLine1("123 Property Lane")
            .addressLine2("Suite 4")
            .townCity("Manchester")
            .county("Greater Manchester")
            .postcode("M1 1AA")
            .ptCase(ptCase)
            .build();

        CasePropertyAddressEntity saved = repository.save(address);

        Optional<CasePropertyAddressEntity> result = repository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isNotNull();
        assertThat(result.get().getAddressLine1()).isEqualTo("123 Property Lane");
        assertThat(result.get().getAddressLine2()).isEqualTo("Suite 4");
        assertThat(result.get().getTownCity()).isEqualTo("Manchester");
        assertThat(result.get().getCounty()).isEqualTo("Greater Manchester");
        assertThat(result.get().getPostcode()).isEqualTo("M1 1AA");
        assertThat(result.get().getPtCase().getId()).isEqualTo(ptCase.getId());
        assertThat(result.get().getPtCase().getCaseReference()).isEqualTo(caseReference);
    }
}
