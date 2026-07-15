package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.FlagReferenceData;

public interface FlagReferenceDataRepository extends JpaRepository<FlagReferenceData, Long> {
}
