package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.ccd.domain.RepresentativeType;
import uk.gov.hmcts.reform.pt.entity.RepresentativeTypeEntity;

import java.util.Optional;

public interface RepresentativeTypeRepository extends JpaRepository<RepresentativeTypeEntity, Long> {
    Optional<RepresentativeTypeEntity> findByTypeName(RepresentativeType typeName);
}
