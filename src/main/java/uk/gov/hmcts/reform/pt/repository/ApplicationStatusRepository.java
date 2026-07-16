package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.ApplicationStatusEntity;

public interface ApplicationStatusRepository extends JpaRepository<ApplicationStatusEntity, Long> {
}
