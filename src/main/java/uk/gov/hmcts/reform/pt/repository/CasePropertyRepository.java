package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CaseProperty;

public interface CasePropertyRepository extends JpaRepository<CaseProperty, Long> {
}
