package uk.gov.hmcts.reform.pt.ccd.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.CitizenAccess;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class ApplicantContactPreferences {
    @CCD(
        label = "Applicant wants to receive text updates",
        access = {CitizenAccess.class}
    )
    private YesOrNo textUpdates;

    @CCD(
        label = "Mobile phone number applicant want to receive text updates for",
        access = {CitizenAccess.class}
    )
    private String textUpdatesPhoneNumber;

    @CCD(
        label = "Applicant wants to receive text updates",
        access = {CitizenAccess.class}
    )
    private String phoneNumberForCalls;
}
