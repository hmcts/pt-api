package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CaseType;

public interface CaseTypeRepository extends JpaRepository<CaseType, Long> {
}
