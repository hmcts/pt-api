package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CasePartyFlagEntity;

public interface CasePartyFlagRepository extends JpaRepository<CasePartyFlagEntity, Long> {
}
