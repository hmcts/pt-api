package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CaseParty;

import java.util.Optional;
import java.util.UUID;

public interface CasePartyRepository extends JpaRepository<CaseParty, Long> {
    Optional<CaseParty> findFirstByAccessIdamId(UUID idamId);
}
