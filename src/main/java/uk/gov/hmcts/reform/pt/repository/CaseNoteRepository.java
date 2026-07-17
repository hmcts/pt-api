package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CaseNoteEntity;

public interface CaseNoteRepository extends JpaRepository<CaseNoteEntity, Long> {
}
