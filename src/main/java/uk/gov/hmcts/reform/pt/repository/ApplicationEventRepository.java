package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.ApplicationEvent;

public interface ApplicationEventRepository extends JpaRepository<ApplicationEvent, Long> {
}
