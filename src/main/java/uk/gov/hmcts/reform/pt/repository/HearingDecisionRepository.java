package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.HearingDecisionEntity;

public interface HearingDecisionRepository extends JpaRepository<HearingDecisionEntity, Long> {
}
