package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.HardshipConsiderationEntity;

public interface HardshipConsiderationRepository extends JpaRepository<HardshipConsiderationEntity, Long> {
}
