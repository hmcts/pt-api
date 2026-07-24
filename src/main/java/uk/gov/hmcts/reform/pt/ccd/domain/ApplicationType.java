package uk.gov.hmcts.reform.pt.ccd.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.hmcts.ccd.sdk.api.HasLabel;

@Getter
@AllArgsConstructor
public enum ApplicationType implements HasLabel {
    @JsonProperty("challengeRentIncrease")
    CHALLENGE_RENT_INCREASE("Challenge Rent Increase"),

    @JsonProperty("challengeExcessiveRent")
    CHALLENGE_EXCESSIVE_RENT("Challenge Excessive Rent");

    private final String label;
}
