package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.ccd.domain.TenancyType;
import uk.gov.hmcts.reform.pt.entity.TenancyDetailsEntity;

import java.util.Optional;

public interface TenancyDetailsRepository extends JpaRepository<TenancyDetailsEntity, Long> {
    Optional<TenancyDetailsEntity> findFirstByTenancyType(TenancyType tenancyType);
}
