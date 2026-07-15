package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.HearingDecision;

public interface HearingDecisionRepository extends JpaRepository<HearingDecision, Long> {
}
