package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CaseHearingEntity;

public interface CaseHearingRepository extends JpaRepository<CaseHearingEntity, Long> {
}
