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
public class LandlordResponse {
    @CCD(
        label = "Responding to the tenant's answers about tenancy details",
        access = {CitizenAccess.class}
    )
    private YesOrNo respondToTenantAnswersAboutTenancyDetails;

    @CCD(
        label = "Response to the tenant's answers about tenancy details",
        access = {CitizenAccess.class}
    )
    private String respondToTenantAnswersAboutTenancyDetailsDescription;
}
