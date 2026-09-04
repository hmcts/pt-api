package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.DocumentEntity;

import java.util.Optional;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

    long deleteByIdAndPtCaseCaseReference(Long id, Long caseReference);

    Optional<DocumentEntity> findByIdAndPtCaseCaseReference(Long id, Long caseReference);
}
