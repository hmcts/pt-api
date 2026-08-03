package uk.gov.hmcts.reform.pt.ccd.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.hmcts.ccd.sdk.api.HasLabel;

@Getter
@AllArgsConstructor
public enum PropertyType implements HasLabel {
    @JsonProperty("room")
    ROOM("Room"),

    @JsonProperty("flat")
    FLAT("Flat"),

    @JsonProperty("terracedHouse")
    TERRACED_HOUSE("Terraced House"),

    @JsonProperty("semiDetachedHouse")
    SEMI_DETACHED_HOUSE("Semi Detached House"),

    @JsonProperty("fullyDetachedHouse")
    FULLY_DETACHED_HOUSE("Fully Detached House"),

    @JsonProperty("other")
    OTHER("Other");

    private final String label;
}
