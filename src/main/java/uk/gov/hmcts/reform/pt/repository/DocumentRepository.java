package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
