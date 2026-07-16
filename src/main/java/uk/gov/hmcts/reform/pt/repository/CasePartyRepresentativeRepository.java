package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CasePartyRepresentativeEntity;

public interface CasePartyRepresentativeRepository extends JpaRepository<CasePartyRepresentativeEntity, Long> {
}
