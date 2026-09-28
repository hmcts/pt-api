package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CasePartyFlagEntity;

import java.util.UUID;

public interface CasePartyFlagRepository extends JpaRepository<CasePartyFlagEntity, UUID> {
}
