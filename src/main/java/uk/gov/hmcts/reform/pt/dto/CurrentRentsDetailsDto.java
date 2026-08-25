package uk.gov.hmcts.reform.pt.dto;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.ccd.domain.Frequency;
import uk.gov.hmcts.reform.pt.ccd.domain.YesNoNotSure;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CurrentRentsDetailsDto {
    private YesOrNo tribunalPreviouslyDeterminedTenancyRent;
    private String previousTribunalCaseReference;

    private Frequency rentPaymentFrequency;
    private BigDecimal rentCostWeekly;
    private BigDecimal rentCostFortnightly;
    private BigDecimal rentCostMonthly;
    private BigDecimal rentCostYearly;

    private YesOrNo rentIncludesCouncilTax;
    private Frequency councilTaxFrequency;
    private BigDecimal councilTaxCostWeekly;
    private BigDecimal councilTaxCostFortnightly;
    private BigDecimal councilTaxCostMonthly;
    private BigDecimal councilTaxCostYearly;
    private String councilTaxFrequencyAndCostDetails;

    private Frequency utilitiesPaidFrequency;
    private BigDecimal utilitiesCostWeekly;
    private BigDecimal utilitiesCostFortnightly;
    private BigDecimal utilitiesCostMonthly;
    private BigDecimal utilitiesCostYearly;
    private String utilitiesPaidFrequencyAndCostDetails;

    private LocalDateTime currentTenancyStartDate;
    private LocalDateTime currentTenancyEndDate;
    private YesNoNotSure currentTenancyReplaceOriginalTenancy;
    private LocalDateTime originalTenancyStartDate;

    private YesOrNo additionalRentalServiceChargesVary;
    private String additionalRentalVaryingServiceChargesDetails;

    // todo charged separately for anything else
    // todo other charges
}
