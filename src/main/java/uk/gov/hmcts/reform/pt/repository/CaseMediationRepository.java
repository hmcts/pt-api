package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CaseMediation;

public interface CaseMediationRepository extends JpaRepository<CaseMediation, Long> {
}
