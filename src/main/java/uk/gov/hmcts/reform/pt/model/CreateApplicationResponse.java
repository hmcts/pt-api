package uk.gov.hmcts.reform.pt.model;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class CreateApplicationResponse {
    private long caseReference;
}
