package uk.gov.hmcts.reform.pt.ccd.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.hmcts.ccd.sdk.api.HasLabel;

@Getter
@AllArgsConstructor
public enum PartyRole implements HasLabel {
    // cannot exceed 100 chars in db

    @JsonProperty("applicant")
    APPLICANT("Applicant"),

    @JsonProperty("landlord")
    LANDLORD("Landlord"),

    @JsonProperty("landlordRepresentative")
    LANDLORD_REPRESENTATIVE("Landlord representative"),

    @JsonProperty("lettingAgent")
    LETTING_AGENT("Letting agent");

    private final String label;
}
