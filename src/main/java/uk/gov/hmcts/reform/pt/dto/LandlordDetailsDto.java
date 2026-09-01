package uk.gov.hmcts.reform.pt.dto;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.pt.ccd.domain.LandlordRepresentativeType;

@Data
@Builder
public class LandlordDetailsDto {
    private LandlordRepresentativeType landlordRepresentativeType;
    private PartyDto landlord;
    private PartyDto representative;
    private PartyDto lettingAgent;
}
