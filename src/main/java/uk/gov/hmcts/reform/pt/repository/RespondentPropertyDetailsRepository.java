package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.RespondentPropertyDetailsEntity;

public interface RespondentPropertyDetailsRepository
    extends JpaRepository<RespondentPropertyDetailsEntity, Long> {
}
