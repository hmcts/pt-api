package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.ApplicationRetention;

public interface ApplicationRetentionRepository extends JpaRepository<ApplicationRetention, Long> {
}
