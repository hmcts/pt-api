package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.NonRentCase;

public interface NonRentCaseRepository extends JpaRepository<NonRentCase, Long> {
}
