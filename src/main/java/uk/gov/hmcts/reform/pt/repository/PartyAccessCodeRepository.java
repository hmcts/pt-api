package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.PartyAccessCodeEntity;

import java.util.List;
import java.util.Optional;

public interface PartyAccessCodeRepository extends JpaRepository<PartyAccessCodeEntity, Long> {

    Optional<PartyAccessCodeEntity> findByPtCaseIdAndPartyId(Long ptCaseId, Long partyId);

    List<PartyAccessCodeEntity> findAllByPtCaseCaseReference(Long caseReference);
}
