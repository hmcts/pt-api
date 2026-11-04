package uk.gov.hmcts.reform.pt.ccd.domain;

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
public class ExtraSupportDetails {
    @CCD(
        label = "Does the applicant party require extra support?",
        access = {CitizenAccess.class}
    )
    private YesOrNo extraSupportRequired;

    @CCD(
        label = "Extra support required details",
        access = {CitizenAccess.class}
    )
    private String extraSupportRequiredDetails;
}
