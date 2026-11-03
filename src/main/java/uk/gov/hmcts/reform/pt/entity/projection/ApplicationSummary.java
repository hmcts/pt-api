package uk.gov.hmcts.reform.pt.entity.projection;

import uk.gov.hmcts.reform.pt.ccd.domain.State;

import java.time.LocalDateTime;

public interface ApplicationSummary {

    Long getId();

    long getCaseReference();

    LocalDateTime getCreatedDate();

    LocalDateTime getSubmittedDate();

    State getState();
}
