package uk.gov.hmcts.reform.pt.ccd.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.hmcts.ccd.sdk.api.HasLabel;

@Getter
@AllArgsConstructor
public enum Frequency implements HasLabel {
    @JsonProperty("weekly")
    WEEKLY("Weekly"),

    @JsonProperty("fortnightly")
    FORTNIGHTLY("Fortnightly"),

    @JsonProperty("monthly")
    MONTHLY("Monthly"),

    @JsonProperty("yearly")
    YEARLY("Yearly");

    private final String label;
}
