package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CaseEvent;

public interface CaseEventRepository extends JpaRepository<CaseEvent, Long> {
}
