package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.pt.entity.RepresentativeTypeEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RepresentativeTypeRepositoryTest extends AbstractRepositoryTest<RepresentativeTypeRepository> {

    @Autowired
    protected RepresentativeTypeRepositoryTest(RepresentativeTypeRepository repository) {
        super(repository);
    }

    @Test
    @DisplayName("Should save and retrieve representative type entity")
    void saveAndFindRepresentativeType() {
        RepresentativeTypeEntity type = RepresentativeTypeEntity.builder().typeName("LETTING_AGENT").build();

        RepresentativeTypeEntity saved = repository.save(type);

        Optional<RepresentativeTypeEntity> result = repository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isNotNull();
        assertThat(result.get().getTypeName()).isEqualTo("LETTING_AGENT");
    }
}
