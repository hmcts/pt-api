package uk.gov.hmcts.reform.pt.dto;

import lombok.Builder;
import lombok.Getter;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;

import java.math.BigDecimal;

@Getter
@Builder
public class MarketRentDto {
    private BigDecimal applicantSuggestedMarketRent;
    private String applicantSuggestedMarketRentReasons;

    private DocumentDto suggestedMarketRentEvidence;

    private YesOrNo additionalPropertyInfoToConsiderWhenDetermining;
    private String additionalPropertyInfoToConsiderWhenDeterminingDetails;
}
