package uk.gov.hmcts.reform.pt.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateApplicationResponseDto {
    private long caseReference;
}
