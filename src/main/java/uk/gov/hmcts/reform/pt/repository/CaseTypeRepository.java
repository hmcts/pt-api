package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CaseType;

import java.util.Optional;

public interface CaseTypeRepository extends JpaRepository<CaseType, Long> {
    Optional<CaseType> findFirstByApplicationTypeName(String applicationTypeName);
}
