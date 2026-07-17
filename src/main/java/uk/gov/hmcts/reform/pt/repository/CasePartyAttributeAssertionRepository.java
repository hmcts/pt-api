package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CasePartyAttributeEntity;

public interface CasePartyAttributeAssertionRepository extends JpaRepository<CasePartyAttributeEntity, Long> {
}
