package uk.gov.hmcts.reform.pt.repository.reference;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.reference.ChangeReasonEntity;

public interface ChangeReasonRepository extends JpaRepository<ChangeReasonEntity, String> {
}
