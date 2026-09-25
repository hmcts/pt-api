package uk.gov.hmcts.reform.pt.entity.projection;

import java.time.LocalDateTime;

public interface ApplicationSummary {

    Long getId();

    long getCaseReference();

    LocalDateTime getCreatedDate();

    LocalDateTime getSubmittedDate();
}
