package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PTCaseRepository extends JpaRepository<PTCaseEntity, UUID> {

    Optional<PTCaseEntity> findByCaseReference(long caseReference);

    Optional<PTCaseEntity> findByCaseReferenceAndIdamUserId(long caseReference, UUID idamUserId);

    List<PTCaseEntity> findAllByIdamUserId(UUID idamUserId);
}
