package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.HelpWithFees;

public interface HelpWithFeesRepository extends JpaRepository<HelpWithFees, Long> {
}
