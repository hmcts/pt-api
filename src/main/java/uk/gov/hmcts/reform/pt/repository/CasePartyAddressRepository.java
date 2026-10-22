package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CasePartyAddress;

public interface CasePartyAddressRepository extends JpaRepository<CasePartyAddress, Long> {
}
