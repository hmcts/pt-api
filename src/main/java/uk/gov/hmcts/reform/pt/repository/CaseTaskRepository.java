package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CaseTaskEntity;

public interface CaseTaskRepository extends JpaRepository<CaseTaskEntity, Long> {
}
