package uk.gov.hmcts.reform.pt.ccd.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.CitizenAccess;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class CurrentRentDetails {
    @CCD(
        label = "Tribunal previously determined tenancy rent",
        access = {CitizenAccess.class}
    )
    private YesOrNo tribunalPreviouslyDeterminedTenancyRent;

    @CCD(
        label = "Previous tribunal case reference",
        access = {CitizenAccess.class}
    )
    private String previousTribunalCaseReference;

    @CCD(
        label = "Rent payment frequency",
        access = {CitizenAccess.class}
    )
    private Frequency rentPaymentFrequency;

    @CCD(
        label = "Weekly rent cost",
        access = {CitizenAccess.class}
    )
    private BigDecimal rentCostWeekly;

    @CCD(
        label = "Rent cost fortnightly",
        access = {CitizenAccess.class}
    )
    private BigDecimal rentCostFortnightly;

    @CCD(
        label = "Rent cost monthly",
        access = {CitizenAccess.class}
    )
    private BigDecimal rentCostMonthly;

    @CCD(
        label = "Rent cost yearly",
        access = {CitizenAccess.class}
    )
    private BigDecimal rentCostYearly;

    @CCD(
        label = "Rent includes council tax",
        access = {CitizenAccess.class}
    )
    private YesOrNo rentIncludesCouncilTax;

    @CCD(
        label = "Council tax frequency",
        access = {CitizenAccess.class}
    )
    private Frequency councilTaxFrequency;

    @CCD(
        label = "Weekly council tax cost",
        access = {CitizenAccess.class}
    )
    private BigDecimal councilTaxCostWeekly;

    @CCD(
        label = "Fortnightly council tax cost",
        access = {CitizenAccess.class}
    )
    private BigDecimal councilTaxCostFortnightly;

    @CCD(
        label = "Monthly council tax cost",
        access = {CitizenAccess.class}
    )
    private BigDecimal councilTaxCostMonthly;

    @CCD(
        label = "Yearly council tax cost",
        access = {CitizenAccess.class}
    )
    private BigDecimal councilTaxCostYearly;

    @CCD(
        label = "Council tax frequency and cost details",
        access = {CitizenAccess.class}
    )
    private String councilTaxFrequencyAndCostDetails;

    @CCD(
        label = "Utilities paid frequency",
        access = {CitizenAccess.class}
    )
    private Frequency utilitiesPaidFrequency;

    @CCD(
        label = "Weekly utilities cost",
        access = {CitizenAccess.class}
    )
    private BigDecimal utilitiesPaidCostWeekly;

    @CCD(
        label = "Fortnightly utilities cost",
        access = {CitizenAccess.class}
    )
    private BigDecimal utilitiesPaidCostFortnightly;

    @CCD(
        label = "Monthly utilities cost",
        access = {CitizenAccess.class}
    )
    private BigDecimal utilitiesPaidCostMonthly;

    @CCD(
        label = "Yearly utilities cost",
        access = {CitizenAccess.class}
    )
    private BigDecimal utilitiesPaidCostYearly;

    @CCD(
        label = "Utilities paid frequency and cost details",
        access = {CitizenAccess.class}
    )
    private String utilitiesPaidFrequencyAndCostDetails;

    @CCD(
        label = "Current tenancy start date",
        access = {CitizenAccess.class}
    )
    private LocalDateTime currentTenancyStartDate;

    @CCD(
        label = "Current tenancy end date",
        access = {CitizenAccess.class}
    )
    private LocalDateTime currentTenancyEndDate;

    @CCD(
        label = "Does current tenancy replace original tenancy",
        access = {CitizenAccess.class}
    )
    private YesNoNotSure currentTenancyReplaceOriginalTenancy;

    @CCD(
        label = "Original tenancy start date",
        access = {CitizenAccess.class}
    )
    private LocalDateTime originalTenancyStartDate;

    @CCD(
        label = "Additional charges vary",
        access = {CitizenAccess.class}
    )
    private YesOrNo additionalRentalServiceChargesVary;

    @CCD(
        label = "Additional charges vary details",
        access = {CitizenAccess.class}
    )
    private String additionalRentalVaryingServiceChargesDetails;

    // todo charged separately for anything else
    // todo other charges
}
