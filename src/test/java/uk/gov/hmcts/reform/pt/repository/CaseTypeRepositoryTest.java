package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicationType;
import uk.gov.hmcts.reform.pt.entity.reference.CaseTypeEntity;
import uk.gov.hmcts.reform.pt.repository.reference.CaseTypeRepository;

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
        ApplicationType applicationType = ApplicationType.CHALLENGE_EXCESSIVE_RENT;
        CaseTypeEntity caseType = CaseTypeEntity.builder()
            .key(applicationType.toString())
            .valueEn(applicationType)
            .build();
        repository.save(caseType);

        Optional<CaseTypeEntity> result = repository.findFirstByValueEn(applicationType);

        assertThat(result).isPresent();
        assertThat(result.get().getValueEn()).isEqualTo(applicationType);
    }
}
