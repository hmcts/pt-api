package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;

import java.util.Optional;

public interface PTCaseRepository extends JpaRepository<PTCaseEntity, Long> {
    Optional<PTCaseEntity> findByCaseReference(long caseReference);
}
