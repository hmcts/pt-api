package uk.gov.hmcts.reform.pt.ccd.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.CitizenAccess;

@Builder
@Data
public class PTCase {
    @CCD(
        label = "Applicant's first name",
        access = {CitizenAccess.class}
    )
    private String applicantFirstName;

    @CCD(
        label = "Applicant's last name",
        access = {CitizenAccess.class}
    )
    private String applicantLastName;

    @CCD(
        label = "Applicant's email address",
        access = {CitizenAccess.class}
    )
    private String email;

    @CCD(
        label = "Applicant's postcode",
        access = {CitizenAccess.class}
    )
    private String postcode;

    @CCD(
        label = "Applicant applying for themselves or on behalf of someone else",
        access = {CitizenAccess.class}
    )
    private ApplicationType applicationType;

    @CCD(
        label = "What type of tenancy is the applicant applying about",
        access = {CitizenAccess.class}
    )
    private TenancyType tenancyType;

    @JsonUnwrapped
    private ApplicantContactPreferences applicantContactPreferences;
}
