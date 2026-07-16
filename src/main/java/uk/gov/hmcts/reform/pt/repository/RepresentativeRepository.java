package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.RepresentativeEntity;

public interface RepresentativeRepository extends JpaRepository<RepresentativeEntity, Long> {
}
