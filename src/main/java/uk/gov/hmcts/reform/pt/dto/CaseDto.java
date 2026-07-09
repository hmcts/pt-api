package uk.gov.hmcts.reform.pt.dto;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicationType;

import java.util.UUID;

@Data
@Builder
public class CaseDto {
    private UUID id;
    private long caseReference;
    private String applicantFirstName;
    private String applicantLastName;
    private String email;
    private String postcode;
    private UUID idamUserId;
    private ApplicationType applicationType;
}
