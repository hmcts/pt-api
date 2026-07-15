package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CasePartyAttribute;

public interface CasePartyAttributeAssertionRepository extends JpaRepository<CasePartyAttribute, Long> {
}
