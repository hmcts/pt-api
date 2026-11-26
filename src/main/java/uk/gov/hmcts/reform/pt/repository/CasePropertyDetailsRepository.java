package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CasePropertyDetailsEntity;

public interface CasePropertyDetailsRepository extends JpaRepository<CasePropertyDetailsEntity, Long> {
}
