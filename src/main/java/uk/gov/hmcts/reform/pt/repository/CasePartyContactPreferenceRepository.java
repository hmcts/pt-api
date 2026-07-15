package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CasePartyContactPreference;

public interface CasePartyContactPreferenceRepository extends JpaRepository<CasePartyContactPreference, Long> {
}
