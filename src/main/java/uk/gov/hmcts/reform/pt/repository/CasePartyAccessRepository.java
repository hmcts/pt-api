package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CasePartyAccessEntity;

public interface CasePartyAccessRepository extends JpaRepository<CasePartyAccessEntity, Long> {
}
