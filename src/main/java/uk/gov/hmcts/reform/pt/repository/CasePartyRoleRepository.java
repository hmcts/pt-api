package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CasePartyRole;

public interface CasePartyRoleRepository extends JpaRepository<CasePartyRole, Long> {
}
