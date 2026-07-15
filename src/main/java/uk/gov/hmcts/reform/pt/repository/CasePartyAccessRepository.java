package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CasePartyAccess;

public interface CasePartyAccessRepository extends JpaRepository<CasePartyAccess, Long> {
}
