package uk.gov.hmcts.reform.pt.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApplicationDto {
    private long caseReference;
    private LocalDateTime createdDate;
    private LocalDateTime submittedOn;
}
