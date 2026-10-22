package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CasePropertyAddressEntity;

public interface CasePropertyAddressRepository extends JpaRepository<CasePropertyAddressEntity, Long> {
}
