package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.DecisionAppeal;

public interface DecisionAppealRepository extends JpaRepository<DecisionAppeal, Long> {
}
