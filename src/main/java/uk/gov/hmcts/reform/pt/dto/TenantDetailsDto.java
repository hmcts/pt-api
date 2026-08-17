package uk.gov.hmcts.reform.pt.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TenantDetailsDto {
    private String firstName;
    private String lastName;
    private String companyName;
    private String referenceNumberForCommunications;
}
