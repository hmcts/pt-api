package uk.gov.hmcts.reform.pt.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyRole;
import uk.gov.hmcts.reform.pt.entity.CasePartyRoleEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class CasePartyRoleRepositoryTest extends AbstractRepositoryTest<CasePartyRoleRepository> {

    @Autowired
    protected CasePartyRoleRepositoryTest(CasePartyRoleRepository repository) {
        super(repository);
    }

    @Test
    @DisplayName("Should return case party role for role name")
    public void findFirstByRoleNameReturnsCasePartyRole() {
        PartyRole roleName = PartyRole.APPLICANT;
        CasePartyRoleEntity role = CasePartyRoleEntity.builder()
            .roleName(roleName)
            .build();
        repository.save(role);

        Optional<CasePartyRoleEntity> result = repository.findFirstByRoleName(roleName);

        assertThat(result).isPresent();
        assertThat(result.get().getRoleName()).isEqualTo(roleName);
    }
}
