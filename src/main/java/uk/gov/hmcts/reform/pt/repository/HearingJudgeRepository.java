package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.HearingJudgeEntity;

public interface HearingJudgeRepository extends JpaRepository<HearingJudgeEntity, Long> {
}
