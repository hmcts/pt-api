package uk.gov.hmcts.reform.pt.ccd.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.hmcts.ccd.sdk.api.HasLabel;

@Getter
@AllArgsConstructor
public enum LandlordRepresentativeType implements HasLabel {
    @JsonProperty("lettingAgent")
    LETTING_AGENT("Letting agent"),

    @JsonProperty("representative")
    REPRESENTATIVE("Representative"),

    @JsonProperty("lettingAgentAndRepresentative")
    LETTING_AGENT_AND_REPRESENTATIVE("Letting agent and representative"),

    @JsonProperty("noLettingAgentOrRepresentative")
    NO_LETTING_AGENT_OR_REPRESENTATIVE("No letting agent or representative"),

    @JsonProperty("notSure")
    NOT_SURE("I'm not sure");

    private final String label;
}
