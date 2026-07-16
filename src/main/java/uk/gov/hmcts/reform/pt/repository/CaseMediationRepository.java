package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CaseMediationEntity;

public interface CaseMediationRepository extends JpaRepository<CaseMediationEntity, Long> {
}
