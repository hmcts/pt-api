package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.HearingPartyEntity;

public interface HearingPartyRepository extends JpaRepository<HearingPartyEntity, Long> {
}
