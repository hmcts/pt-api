package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.HearingInspection;

public interface HearingInspectionRepository extends JpaRepository<HearingInspection, Long> {
}
