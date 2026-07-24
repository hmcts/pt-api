package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PTCaseRepositoryTest extends AbstractRepositoryTest<PTCaseRepository> {

    @Autowired
    protected PTCaseRepositoryTest(PTCaseRepository repository) {
        super(repository);
    }

    @Test
    void findByCaseReference_returnsEntity_whenExists() {
        PTCaseEntity entity = PTCaseEntity.builder()
            .caseReference(1234567890123456L)
            .build();
        repository.save(entity);

        Optional<PTCaseEntity> result = repository.findByCaseReference(1234567890123456L);

        assertThat(result).isPresent();
        assertThat(result.get().getCaseReference()).isEqualTo(1234567890123456L);
    }
}
