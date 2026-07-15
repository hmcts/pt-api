package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.Representative;

public interface RepresentativeRepository extends JpaRepository<Representative, Long> {
}
