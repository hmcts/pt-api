package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.NonRentCaseEntity;

public interface NonRentCaseRepository extends JpaRepository<NonRentCaseEntity, Long> {
}
