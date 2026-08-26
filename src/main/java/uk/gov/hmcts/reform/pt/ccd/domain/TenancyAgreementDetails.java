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
public class TenancyAgreementDetails {
    @CCD(
        label = "Do you have a copy of the tenancy agreement?",
        access = {CitizenAccess.class}
    )
    private YesOrNo copyOfTenancyAgreement;

    @CCD(
        label = "Reason for no copy of tenancy agreement",
        access = {CitizenAccess.class}
    )
    private String noTenancyAgreementReason;

    @CCD(
        label = "Tenancy agreement document",
        access = {CitizenAccess.class}
    )
    private UploadedDocument tenancyAgreementDocument;
}
