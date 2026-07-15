package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.gov.hmcts.reform.pt.entity.CaseApplication;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CaseApplicationRepository extends JpaRepository<CaseApplication, Long> {

    List<CaseApplication> findAllByCasePartyAccessIdamId(UUID idamId);

    @Query("""
        SELECT ca FROM CaseApplication ca
        JOIN ca.caseParty cp
        JOIN cp.access cpa
        JOIN cp.ptCase pc
        WHERE cpa.idamId = :idamId
        AND pc.caseReference = :caseReference
        """)
    Optional<CaseApplication> findByPartyIdamIdAndCaseReference(
        @Param("caseReference") Long caseReference,
        @Param("idamId") UUID idamId
    );
}
