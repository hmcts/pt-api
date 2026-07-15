package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.ClaimType;

public interface ClaimTypeRepository extends JpaRepository<ClaimType, Long> {
}
