package uk.gov.hmcts.reform.pt.ccd.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.CitizenAccess;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantDetails {
    @CCD(
        label = "Tenant's company name",
        access = {CitizenAccess.class}
    )
    private String companyName;

    @CCD(
        label = "Tenant's reference number for communications",
        access = {CitizenAccess.class}
    )
    private String referenceNumberForCommunications;
}
