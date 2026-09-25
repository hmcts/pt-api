package uk.gov.hmcts.reform.pt.repository.reference;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.reference.CustodyStatusEntity;

public interface CustodyStatusRepository extends JpaRepository<CustodyStatusEntity, String> {
}
