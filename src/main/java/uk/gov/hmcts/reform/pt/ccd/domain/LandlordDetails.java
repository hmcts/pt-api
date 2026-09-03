package uk.gov.hmcts.reform.pt.ccd.domain;

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
public class LandlordDetails {

    @CCD(
        label = "Landlord Party Details",
        access = {CitizenAccess.class}
    )
    private PartyDetails landlordPartyDetails;

    @CCD(
        label = "Landlord Representative Type",
        access = {CitizenAccess.class}
    )
    private LandlordRepresentativeType representativeType;

    @CCD(
        label = "Letting Agent Party Details",
        access = {CitizenAccess.class}
    )
    private PartyDetails lettingAgentPartyDetails;

    @CCD(
        label = "Representative Party Details",
        access = {CitizenAccess.class}
    )
    private PartyDetails representativePartyDetails;


}
