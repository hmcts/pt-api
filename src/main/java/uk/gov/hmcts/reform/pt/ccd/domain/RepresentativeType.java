package uk.gov.hmcts.reform.pt.ccd.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.hmcts.ccd.sdk.api.HasLabel;

@Getter
@AllArgsConstructor
public enum RepresentativeType implements HasLabel {
    @JsonProperty("landlordRepresentative")
    LANDLORD_REPRESENTATIVE("Landlord representative");

    private final String label;
}
