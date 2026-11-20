package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.MissingInformationRequestEntity;

public interface MissingInformationRequestRepository extends JpaRepository<MissingInformationRequestEntity, Long> {
}
