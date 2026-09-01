package uk.gov.hmcts.reform.pt.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PartyDto {
    private String firstName;
    private String lastName;
    private String organisationName;
    private String emailAddress;
    private String phoneNumber;
    private String dxNumber;

    private String addressLine1;
    private String addressLine2;
    private String postTown;
    private String county;
    private String postcode;
}
