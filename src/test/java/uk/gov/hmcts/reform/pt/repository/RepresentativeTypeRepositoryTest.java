package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.pt.ccd.domain.RepresentativeType;
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
        RepresentativeTypeEntity saved = repository.save(
            RepresentativeTypeEntity.builder().typeName(RepresentativeType.LANDLORD_REPRESENTATIVE).build());

        Optional<RepresentativeTypeEntity> result = repository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTypeName()).isEqualTo(RepresentativeType.LANDLORD_REPRESENTATIVE);
    }

    @Test
    @DisplayName("Should find representative type by type name")
    void findByTypeName() {
        repository.save(
            RepresentativeTypeEntity.builder().typeName(RepresentativeType.LANDLORD_REPRESENTATIVE).build());

        assertThat(repository.findByTypeName(RepresentativeType.LANDLORD_REPRESENTATIVE)).isPresent();
    }
}
