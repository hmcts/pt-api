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
    private PartyDetails landlord;

    @CCD(
        label = "Landlord has representative",
        access = {CitizenAccess.class}
    )
    private YesNoNotSure hasRepresentative;

    @CCD(
        label = "Representative Party Details",
        access = {CitizenAccess.class}
    )
    private PartyDetails representative;
}
