package uk.gov.hmcts.reform.pt.ccd.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.hmcts.ccd.sdk.api.HasLabel;

@Getter
@AllArgsConstructor
public enum YesNoNotSure implements HasLabel {
    @JsonProperty("yes")
    YES("Yes"),

    @JsonProperty("no")
    NO("No"),

    @JsonProperty("notSure")
    NOT_SURE("I’m not sure");

    private final String label;
}
