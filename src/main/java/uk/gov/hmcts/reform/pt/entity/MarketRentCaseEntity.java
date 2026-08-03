package uk.gov.hmcts.reform.pt.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.ccd.domain.Frequency;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "market_rent_case")
public class MarketRentCaseEntity extends AuditableEntity {
    @Version
    private Integer version;

    @Column(length = 100)
    private String method;

    @Column(length = 100)
    private String channel;

    @Column(length = 100)
    private String status;

    private LocalDateTime submittedDate;
    private LocalDateTime issuedDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private YesOrNo propertyFloorPlanAvailable;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private PropertyType typeOfPropertyRenting;

    @Column(length = 500)
    private String rentingFlatDetails;

    @Column(length = 500)
    private String rentingRoomsDetails;

    @Column(length = 500)
    private String otherMethodOfRentDetails;

    @Column(length = 5000)
    private String floorplanManualDetails;

    @Column(length = 5000)
    private String propertyIndoorFeatures;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private YesOrNo sharePropertyWithLandlord;

    @Column(length = 5000)
    private String sharePropertyWithLandlordDetails;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private Frequency rentPaymentFrequency;

    private BigDecimal rentCostWeekly;
    private BigDecimal rentCostFortnightly;
    private BigDecimal rentCostMonthly;
    private BigDecimal rentCostYearly;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private YesOrNo rentIncludesCouncilTax;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private Frequency councilTaxFrequency;

    private BigDecimal councilTaxCostWeekly;
    private BigDecimal councilTaxCostFortnightly;
    private BigDecimal councilTaxCostMonthly;
    private BigDecimal councilTaxCostYearly;

    @Column(length = 500)
    private String councilTaxFrequencyAndCostDetails;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private Frequency utilitiesPaidFrequency;

    private BigDecimal utilitiesCostWeekly;
    private BigDecimal utilitiesCostFortnightly;
    private BigDecimal utilitiesCostMonthly;
    private BigDecimal utilitiesCostYearly;

    @Column(length = 500)
    private String utilitiesFrequencyAndCostDetails;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private YesOrNo otherHouseholdManagementCharges;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private YesOrNo rentInclusiveOfUtilityCharges;

    private BigDecimal applicantSuggestedMonthlyMarketRent;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private YesOrNo additionalRentalServiceChargesVary;

    @Column(length = 5000)
    private String additionalRentalVaryingServiceChargesDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_application_id")
    @JsonBackReference
    private CaseApplicationEntity caseApplication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pt_case_id")
    @JsonBackReference
    private PTCaseEntity ptCase;
}
