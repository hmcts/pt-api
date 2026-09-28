package uk.gov.hmcts.reform.pt.repository.reference;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.reference.HearingChannelEntity;

public interface HearingChannelRepository extends JpaRepository<HearingChannelEntity, String> {
}
