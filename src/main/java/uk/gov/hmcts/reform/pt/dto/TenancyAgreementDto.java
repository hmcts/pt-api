package uk.gov.hmcts.reform.pt.dto;

import lombok.Builder;
import lombok.Getter;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;

@Getter
@Builder
public class TenancyAgreementDto {
    private YesOrNo copyOfTenancyAgreement;
    private String noTenancyAgreementReason;
    private DocumentDto tenancyAgreementEvidence;
}
