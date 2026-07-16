package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CaseTypeEntity;

import java.util.Optional;

public interface CaseTypeRepository extends JpaRepository<CaseTypeEntity, Long> {
    Optional<CaseTypeEntity> findFirstByApplicationTypeName(String applicationTypeName);
}
