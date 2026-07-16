package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CasePartyTypeEntity;

public interface CasePartyTypeRepository extends JpaRepository<CasePartyTypeEntity, Long> {
}
