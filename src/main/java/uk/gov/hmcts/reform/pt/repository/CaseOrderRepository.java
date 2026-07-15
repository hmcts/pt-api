package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CaseOrder;

public interface CaseOrderRepository extends JpaRepository<CaseOrder, Long> {
}
