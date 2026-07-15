package uk.gov.hmcts.reform.pt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.pt.entity.ApplicationStatementOfTruth;

public interface ApplicationStatementOfTruthRepository extends JpaRepository<ApplicationStatementOfTruth, Long> {
}
