package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.projection.ApplicationSummary;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CasePartyRepository extends JpaRepository<CasePartyEntity, Long> {
    // TODO: change to also query by rolename
    Optional<CasePartyEntity> findFirstByPtCaseCaseReference(long caseReference);

    List<CasePartyEntity> findAllByIdamId(UUID idamId);

    @Query(value = """
        SELECT DISTINCT
            pc.id                         AS id,
            pc.case_reference             AS caseReference,
            pc.created_date               AS createdDate,
            pc.application_submitted_date AS submittedDate
        FROM case_party cp
        JOIN pt_case pc ON pc.id = cp.pt_case_id
        JOIN ccd.case_data cd ON cd.reference = pc.case_reference
        WHERE cp.idam_id = :idamId
        AND cd.state <> 'PendingDisposal'
        """, nativeQuery = true)
    List<ApplicationSummary> findActiveApplicationsByIdamId(@Param("idamId") UUID idamId);

    @Query(value = """
        SELECT
            cp.id,
            cp.pt_case_id,
            cp.first_name,
            cp.last_name,
            cp.organisation_name,
            cp.phone_number,
            cp.mobile_phone_number,
            cp.email_address,
            cp.date_of_birth,
            cp.reference_number_for_communications,
            cp.case_party_type_id,
            cp.party_role,
            cp.idam_id,
            cp.contact_by_text,
            cp.created_date,
            cp.last_modified_date,
            cp.last_modified_by
        FROM case_party cp
        JOIN pt_case pc ON pc.id = cp.pt_case_id
        JOIN ccd.case_data cd ON cd.reference = pc.case_reference
        WHERE cp.idam_id = :idamId
        AND pc.case_reference = :caseReference
        AND cd.state <> 'PendingDisposal'
        """, nativeQuery = true)
    Optional<CasePartyEntity> findByIdamIdAndCaseReference(
        @Param("caseReference") Long caseReference,
        @Param("idamId") UUID idamId
    );
}
