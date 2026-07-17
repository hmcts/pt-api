package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.gov.hmcts.reform.pt.entity.CaseApplicationEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CaseApplicationRepository extends JpaRepository<CaseApplicationEntity, Long> {

    List<CaseApplicationEntity> findAllByCasePartyAccessIdamId(UUID idamId);

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
}
