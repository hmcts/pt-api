package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.FeeHelpWithFeesEntity;

public interface FeeHelpWithFeesRepository extends JpaRepository<FeeHelpWithFeesEntity, Long> {
}
