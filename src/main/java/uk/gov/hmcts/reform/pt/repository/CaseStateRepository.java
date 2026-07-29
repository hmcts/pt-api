package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CaseStateEntity;

public interface CaseStateRepository extends JpaRepository<CaseStateEntity, Long> {
}
