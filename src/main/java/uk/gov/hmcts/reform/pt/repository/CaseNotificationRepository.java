package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CaseNotificationEntity;

public interface CaseNotificationRepository extends JpaRepository<CaseNotificationEntity, Long> {
}
