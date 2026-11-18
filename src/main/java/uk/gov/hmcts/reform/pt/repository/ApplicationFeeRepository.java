package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.ApplicationFeeEntity;

import java.util.Optional;

public interface ApplicationFeeRepository extends JpaRepository<ApplicationFeeEntity, Long> {

    Optional<ApplicationFeeEntity> findByRequestReference(String requestReference);
}
