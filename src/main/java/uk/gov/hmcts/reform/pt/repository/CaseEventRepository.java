package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CaseEventEntity;

public interface CaseEventRepository extends JpaRepository<CaseEventEntity, Long> {
}
