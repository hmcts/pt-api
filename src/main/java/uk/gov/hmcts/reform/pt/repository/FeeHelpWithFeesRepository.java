package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.FeeHelpWithFees;

public interface FeeHelpWithFeesRepository extends JpaRepository<FeeHelpWithFees, Long> {
}
