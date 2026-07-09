package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PTCaseRepositoryTest extends AbstractRepositoryTest<PTCaseRepository> {

    @Autowired
    protected PTCaseRepositoryTest(PTCaseRepository repository) {
        super(repository);
    }

    @Test
    void findByCaseReference_returnsEntity_whenExists() {
        PTCaseEntity entity = new PTCaseEntity();
        entity.setCaseReference(12345L);
        repository.save(entity);

        Optional<PTCaseEntity> result = repository.findByCaseReference(12345L);

        assertThat(result).isPresent();
        assertThat(result.get().getCaseReference()).isEqualTo(12345L);
    }

    @Test
    void findAllByIdamUserIdReturnsList() {
        UUID idamUserId = UUID.randomUUID();

        PTCaseEntity entity = new PTCaseEntity();
        entity.setIdamUserId(idamUserId);
        repository.save(entity);

        List<PTCaseEntity> result = repository.findAllByIdamUserId(idamUserId);

        assertThat(result).isNotEmpty();
        assertThat(result.getFirst().getIdamUserId()).isEqualTo(idamUserId);
    }
}
