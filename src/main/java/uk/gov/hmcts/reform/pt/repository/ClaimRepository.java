package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.Claim;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
}
