package uk.gov.hmcts.reform.pt.dto;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;

@Data
@Builder
public class ExtraSupportDto {
    private YesOrNo extraSupportRequired;
    private String extraSupportRequiredDetails;
}
