package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.RepresentativeType;

public interface RepresentativeTypeRepository extends JpaRepository<RepresentativeType, Long> {
}
