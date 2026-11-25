package uk.gov.hmcts.reform.pt.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "legal_officer_referral")
public class LegalOfficerReferralEntity extends AuditableEntity {
    @Column(length = 100)
    private String urgency;

    @Column(length = 500)
    private String referralReasons;

    @Column(length = 500)
    private String referralDetails;

    @Column(length = 500)
    private String recommendedInstructions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_application_vetting_id", nullable = false)
    @JsonBackReference
    private CaseApplicationVettingEntity caseApplicationVetting;
}
