package uk.gov.hmcts.reform.pt.dto;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.pt.ccd.domain.YesNoNotSure;

@Data
@Builder
public class LandlordDetailsDto {
    private PartyDto landlord;
    private YesNoNotSure hasRepresentative;
    private PartyDto representative;
}
