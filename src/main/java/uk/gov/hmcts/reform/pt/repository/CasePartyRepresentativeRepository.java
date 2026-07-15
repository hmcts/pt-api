package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CasePartyRepresentative;

public interface CasePartyRepresentativeRepository extends JpaRepository<CasePartyRepresentative, Long> {
}
