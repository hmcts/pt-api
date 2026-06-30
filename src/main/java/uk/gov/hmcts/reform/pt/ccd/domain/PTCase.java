package uk.gov.hmcts.reform.pt.ccd.domain;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.api.CCD;

@Builder
@Data
public class PTCase {
    @CCD(label = "Applicant's first name")
    private String applicantForename;
}
