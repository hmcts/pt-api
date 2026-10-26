package uk.gov.hmcts.reform.pt.ccd.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.ListValue;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.CitizenAccess;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketRentDetails {
    @CCD(
        label = "Applicant's suggested market rent",
        access = {CitizenAccess.class}
    )
    private Double applicantSuggestedMarketRent;

    @CCD(
        label = "Applicant's suggested market rent reasons",
        access = {CitizenAccess.class}
    )
    private String applicantSuggestedMarketRentReasons;

    @CCD(
        label = "Evidence supporting proposed rent",
        access = {CitizenAccess.class}
    )
    @Builder.Default
    private List<ListValue<UploadedDocument>> suggestedMarketRentEvidenceDocuments = new ArrayList<>();

    @CCD(
        label = "Have any additional property information to consider when determining rent",
        access = {CitizenAccess.class}
    )
    private YesOrNo additionalInfoToConsiderWhenDeterminingRent;

    @CCD(
        label = "Additional property information to consider when determining rent details",
        access = {CitizenAccess.class}
    )
    private String additionalInfoToConsiderWhenDeterminingRentDetails;
}
