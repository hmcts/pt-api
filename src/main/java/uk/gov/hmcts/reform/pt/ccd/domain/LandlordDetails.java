package uk.gov.hmcts.reform.pt.ccd.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.CitizenAccess;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class LandlordDetails {

    @JsonUnwrapped(prefix = "Landlord")
    private PartyDetails landlordPartyDetails;

    @CCD(
        label = "Landlord Representative Type",
        access = {CitizenAccess.class}
    )
    private LandlordRepresentativeType representativeType;

    @JsonUnwrapped(prefix = "LettingAgent")
    private PartyDetails lettingAgentPartyDetails;

    @JsonUnwrapped(prefix = "Representative")
    private PartyDetails representativePartyDetails;


}
