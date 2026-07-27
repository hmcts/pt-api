package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.AddressEntity;

public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
}
