package uk.gov.hmcts.reform.pt.ccd.domain;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.api.CCD;

@Builder
@Data
public class PTCase {
    @CCD(label = "Applicant's first name")
    private String firstName;
    @CCD(label = "Applicant's last name")
    private String lastName;
    @CCD(label = "Applicant's email address")
    private String email;
    @CCD(label = "Applicant's postcode")
    private String postcode;
    @CCD(label = "Applicant applying for themselves or on behalf of someone else")
    private String applicationType;
}
