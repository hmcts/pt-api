package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.LegalOfficerReferralEntity;

public interface LegalOfficerReferralRepository extends JpaRepository<LegalOfficerReferralEntity, Long> {
}
