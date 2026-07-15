package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicationType;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PTCaseRepository extends JpaRepository<PTCaseEntity, UUID> {

    Optional<PTCaseEntity> findByCaseReference(long caseReference);

    Optional<PTCaseEntity> findByCaseReferenceAndApplicantIdamUserId(long caseReference, UUID applicantIdamUserId);

    List<PTCaseEntity> findAllByApplicantIdamUserId(UUID applicantIdamUserId);

    @Modifying
    @Query("""
        UPDATE PTCaseEntity e
        SET e.applicantFirstName = :applicantFirstName,
          e.applicantLastName = :applicantLastName,
          e.email = :email,
          e.postcode = :postcode,
          e.applicationType = :applicationType
        WHERE e.caseReference = :caseReference
        """)
    int updateByCaseReference(
        @Param("caseReference") long caseReference,
        @Param("applicantFirstName") String applicantFirstName,
        @Param("applicantLastName") String applicantLastName,
        @Param("email") String email,
        @Param("postcode") String postcode,
        @Param("applicationType")
        ApplicationType applicationType
    );
}
