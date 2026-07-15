package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.MarketRentCase;

public interface MarketRentCaseRepository extends JpaRepository<MarketRentCase, Long> {
}
