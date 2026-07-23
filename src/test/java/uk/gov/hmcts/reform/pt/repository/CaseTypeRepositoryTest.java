package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicationType;
import uk.gov.hmcts.reform.pt.entity.CaseTypeEntity;

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
        ApplicationType applicationType = ApplicationType.CHALLENGE_NOTICE_LEGAL_VALIDITY;
        CaseTypeEntity caseType = CaseTypeEntity.builder()
            .applicationTypeName(applicationType)
            .build();
        repository.save(caseType);

        Optional<CaseTypeEntity> result = repository.findFirstByApplicationTypeName(applicationType);

        assertThat(result).isPresent();
        assertThat(result.get().getApplicationTypeName()).isEqualTo(applicationType);
    }
}
