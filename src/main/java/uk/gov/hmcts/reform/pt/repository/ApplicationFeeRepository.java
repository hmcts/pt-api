package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.ApplicationFee;

public interface ApplicationFeeRepository extends JpaRepository<ApplicationFee, Long> {
}
