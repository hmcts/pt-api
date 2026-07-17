package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CaseEvidenceEntity;

public interface CaseEvidenceRepository extends JpaRepository<CaseEvidenceEntity, Long> {
}
