package uk.gov.hmcts.reform.pt.dto;

import lombok.Builder;
import lombok.Getter;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.ccd.domain.Frequency;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class MarketRentDto {
    private BigDecimal applicantSuggestedMarketRent;
    private Frequency applicantSuggestedMarketRentFrequency;
    private String applicantSuggestedMarketRentFrequencyOther;
    private String applicantSuggestedMarketRentReasons;

    private List<DocumentDto> suggestedMarketRentEvidence;

    private YesOrNo additionalPropertyInfoToConsiderWhenDetermining;
    private String additionalPropertyInfoToConsiderWhenDeterminingDetails;
}
