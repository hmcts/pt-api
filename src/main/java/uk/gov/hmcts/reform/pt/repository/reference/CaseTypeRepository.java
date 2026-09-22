package uk.gov.hmcts.reform.pt.repository.reference;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicationType;
import uk.gov.hmcts.reform.pt.entity.reference.CaseTypeEntity;

import java.util.Optional;

public interface CaseTypeRepository extends JpaRepository<CaseTypeEntity, Long> {
    Optional<CaseTypeEntity> findFirstByValueEn(ApplicationType valueEn);
}
