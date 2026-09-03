package uk.gov.hmcts.reform.pt.ccd.domain;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.CitizenAccess;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.SuperUserAccess;

import static uk.gov.hmcts.ccd.sdk.type.FieldType.FixedList;

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

    @CCD(label = "Which state are you moving the case to?",
        typeOverride = FixedList,
        typeParameterOverride = "State",
        access = {SuperUserAccess.class}
    )
    private State targetState;

    private ApplicantContactPreferences applicantContactPreferences;

    private TenantDetails tenantDetails;

    private HearingPropertyInspectionDetails hearingInspectionDetails;

    private NoticeOfRentIncreaseDetails noticeOfRentIncreaseDetails;

    private PropertyDetails propertyDetails;
}
