package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CasePartyEventEntity;

public interface CasePartyEventRepository extends JpaRepository<CasePartyEventEntity, Long> {
}
