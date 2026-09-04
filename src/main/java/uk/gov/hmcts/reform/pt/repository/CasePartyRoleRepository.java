package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyRole;
import uk.gov.hmcts.reform.pt.entity.CasePartyRoleEntity;

import java.util.Optional;

public interface CasePartyRoleRepository extends JpaRepository<CasePartyRoleEntity, Long> {
    Optional<CasePartyRoleEntity> findFirstByRoleName(PartyRole roleName);
}
