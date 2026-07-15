package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.CaseNote;

public interface CaseNoteRepository extends JpaRepository<CaseNote, Long> {
}
