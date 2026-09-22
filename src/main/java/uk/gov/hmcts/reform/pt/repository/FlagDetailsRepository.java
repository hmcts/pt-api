package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.FlagDetailsEntity;

public interface FlagDetailsRepository extends JpaRepository<FlagDetailsEntity, String> {
}
