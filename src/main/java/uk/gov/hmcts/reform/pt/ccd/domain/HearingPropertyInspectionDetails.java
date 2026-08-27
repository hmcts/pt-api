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
public class HearingPropertyInspectionDetails {
    @CCD(
        label = "Has the citizen requested a hearing?",
        access = {CitizenAccess.class}
    )
    private YesOrNo hearingRequested;

    @CCD(
        label = "Has the citizen agreed to a decision without a hearing?",
        access = {CitizenAccess.class}
    )
    private YesOrNo agreeToDecisionWithoutInspection;

    @CCD(
        label = "Reason for not agreeing to a decision without a hearing",
        access = {CitizenAccess.class}
    )
    private String noDecisionWithoutInspectionReason;
}
