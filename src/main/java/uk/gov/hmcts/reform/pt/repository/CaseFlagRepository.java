package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CaseFlag;

public interface CaseFlagRepository extends JpaRepository<CaseFlag, Long> {
}
