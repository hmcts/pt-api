package uk.gov.hmcts.reform.pt.dto;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;

@Data
@Builder
public class HearingInspectionDetailsDto {
    private YesOrNo hearingRequested;

    private YesOrNo agreeToDecisionWithoutInspection;
    private String noDecisionWithoutInspectionReason;
}
