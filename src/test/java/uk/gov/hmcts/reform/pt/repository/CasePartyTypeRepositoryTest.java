package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.pt.entity.CasePartyTypeEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CasePartyTypeRepositoryTest extends AbstractRepositoryTest<CasePartyTypeRepository> {

    @Autowired
    protected CasePartyTypeRepositoryTest(CasePartyTypeRepository repository) {
        super(repository);
    }

    @Test
    @DisplayName("Should save and retrieve case party type entity")
    void saveAndFindCasePartyType() {
        CasePartyTypeEntity type = CasePartyTypeEntity.builder().applicationTypeName("APPLICANT").build();

        CasePartyTypeEntity saved = repository.save(type);

        Optional<CasePartyTypeEntity> result = repository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isNotNull();
        assertThat(result.get().getApplicationTypeName()).isEqualTo("APPLICANT");
    }
}
