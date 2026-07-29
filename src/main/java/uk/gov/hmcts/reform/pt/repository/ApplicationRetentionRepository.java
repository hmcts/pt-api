package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.ApplicationRetentionEntity;

public interface ApplicationRetentionRepository extends JpaRepository<ApplicationRetentionEntity, Long> {
}
