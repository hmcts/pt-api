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
public class PartyDetails {
    @CCD(
        label = "First name",
        access = {CitizenAccess.class}
    )
    private String firstName;

    @CCD(
        label = "Last name",
        access = {CitizenAccess.class}
    )
    private String lastName;

    @CCD(
        label = "Organisation name",
        access = {CitizenAccess.class}
    )
    private String organisationName;

    @CCD(
        label = "Address line 1",
        access = {CitizenAccess.class}
    )
    private String addressLine1;

    @CCD(
        label = "Address line 2",
        access = {CitizenAccess.class}
    )
    private String addressLine2;

    @CCD(
        label = "Post town",
        access = {CitizenAccess.class}
    )
    private String postTown;

    @CCD(
        label = "County",
        access = {CitizenAccess.class}
    )
    private String county;

    @CCD(
        label = "Postcode",
        access = {CitizenAccess.class}
    )
    private String postcode;

    @CCD(
        label = "Email address",
        access = {CitizenAccess.class}
    )
    private String emailAddress;

    @CCD(
        label = "Phone number",
        access = {CitizenAccess.class}
    )
    private String phoneNumber;

    @CCD(
        label = "DX number",
        access = {CitizenAccess.class}
    )
    private String dxNumber;
}
