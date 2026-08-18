package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.PropertyInspectionEntity;

public interface PropertyInspectionRepository extends JpaRepository<PropertyInspectionEntity, Long> {
}
