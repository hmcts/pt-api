package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.gov.hmcts.reform.pt.entity.CaseApplicationEntity;
import uk.gov.hmcts.reform.pt.entity.projection.ApplicationSummary;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CaseApplicationRepository extends JpaRepository<CaseApplicationEntity, Long> {

    List<CaseApplicationEntity> findAllByCasePartyAccessIdamId(UUID idamId);

    @Query(value = """
        SELECT DISTINCT
            ca.id             AS id,
            pc.case_reference AS caseReference,
            ca.created_date   AS createdDate,
            ca.submitted_date AS submittedDate
        FROM case_application ca
        JOIN case_party cp ON cp.id = ca.case_party_id
        JOIN case_party_access cpa ON cpa.case_party_id = cp.id
        JOIN pt_case pc ON pc.id = cp.pt_case_id
        JOIN ccd.case_data cd ON cd.reference = pc.case_reference
        WHERE cpa.idam_id = :idamId
        AND cd.state <> 'PendingDisposal'
        """, nativeQuery = true)
    List<ApplicationSummary> findActiveByCasePartyAccessIdamId(@Param("idamId") UUID idamId);

    @Query("""
        SELECT ca FROM CaseApplicationEntity ca
        JOIN ca.caseParty cp
        JOIN cp.access cpa
        JOIN cp.ptCase pc
        WHERE cpa.idamId = :idamId
        AND pc.caseReference = :caseReference
        """)
    Optional<CaseApplicationEntity> findByPartyIdamIdAndCaseReference(
        @Param("caseReference") Long caseReference,
        @Param("idamId") UUID idamId
    );

    @Query("""
        SELECT ca FROM CaseApplicationEntity ca
        JOIN ca.caseParty cp
        JOIN cp.ptCase pc
        WHERE pc.caseReference = :caseReference
        """)
    List<CaseApplicationEntity> findAllByCaseReference(@Param("caseReference") Long caseReference);
}
