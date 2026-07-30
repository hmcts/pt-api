package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CaseFlagEntity;

public interface CaseFlagRepository extends JpaRepository<CaseFlagEntity, Long> {
}
