package uk.gov.hmcts.reform.pt.repository.reference;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.reference.ActualCancellationReasonEntity;

public interface ActualCancellationReasonRepository extends JpaRepository<ActualCancellationReasonEntity, String> {
}
