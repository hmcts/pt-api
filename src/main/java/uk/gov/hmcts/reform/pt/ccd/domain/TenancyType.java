package uk.gov.hmcts.reform.pt.ccd.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.hmcts.ccd.sdk.api.HasLabel;

@Getter
@AllArgsConstructor
public enum TenancyType implements HasLabel {
    @JsonProperty("assuredPeriodicTenancy")
    ASSURED_PERIODIC_TENANCY("Assured periodic tenancy"),

    @JsonProperty("agriculturalOccupancy")
    AGRICULTURAL_OCCUPANCY("Agricultural occupancy");

    private final String label;
}
