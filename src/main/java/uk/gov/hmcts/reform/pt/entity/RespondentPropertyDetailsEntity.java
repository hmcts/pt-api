package uk.gov.hmcts.reform.pt.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "respondent_property_details")
public class RespondentPropertyDetailsEntity extends AuditableEntity {
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private YesOrNo respondToTenantAnswersAboutTenancyDetails;

    @Column(length = 500)
    private String respondToTenantAnswersAboutTenancyDetailsDescription;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private YesOrNo respondToTenantAnswersAboutPropertyDetails;

    @Column(length = 500)
    private String respondToTenantAnswersAboutPropertyDetailsDescription;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private YesOrNo propertyAddressCorrect;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private YesOrNo servedNoticeProposingNewRentProvidedInTenantApplication;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private YesOrNo agreeWithRentProposedByTenant;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private YesOrNo agreeToDecisionWithoutInspection;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private YesOrNo respondToTribunalAboutTenantExperiencingHardshipAnswers;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private YesOrNo agreeToDecisionWithoutHearing;

    @Column(length = 500)
    private String noDecisionWithoutHearingReason;

    @Column(length = 500)
    private String noDecisionWithoutInspectionReason;

    @Column(length = 500)
    private String landlordProposedRentAmountAndReasons;

    @Column(length = 500)
    private String tenantsAnswersExperiencingHardshipResponse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statement_of_truth_id")
    private StatementOfTruthEntity statementOfTruth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_party_id")
    private CasePartyEntity caseParty;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_property_details_id", nullable = false, unique = true)
    @JsonBackReference
    private CasePropertyDetailsEntity casePropertyDetails;
}
