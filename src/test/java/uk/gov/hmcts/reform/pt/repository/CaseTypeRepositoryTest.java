package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.pt.entity.CaseType;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CaseTypeRepositoryTest extends AbstractRepositoryTest<CaseTypeRepository> {

    @Autowired
    protected CaseTypeRepositoryTest(CaseTypeRepository repository) {
        super(repository);
    }

    @Test
    @DisplayName("Should return case type for application type name")
    void findFirstByApplicationTypeNameReturnsCaseType() {
        String typeName = "Possession";
        CaseType caseType = CaseType.builder()
            .applicationTypeName(typeName)
            .build();
        repository.save(caseType);

        Optional<CaseType> result = repository.findFirstByApplicationTypeName(typeName);

        assertThat(result).isPresent();
        assertThat(result.get().getApplicationTypeName()).isEqualTo(typeName);
    }
}
