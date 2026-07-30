package uk.gov.hmcts.reform.pt.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "case_application")
public class CaseApplicationEntity extends AuditableEntity {
    private LocalDateTime submittedDate;
    private LocalDateTime issuedDate;

    @Column(length = 100)
    private String status;

    @Column(length = 100)
    private String language;

    @Column(length = 100)
    private String dxNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_type_id")
    private CaseTypeEntity caseType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_party_id")
    private CasePartyEntity caseParty;

    @OneToMany(mappedBy = "caseApplication", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<ApplicationEventEntity> applicationEvents = new ArrayList<>();

    @OneToMany(mappedBy = "caseApplication", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<ApplicationFeeEntity> applicationFees = new ArrayList<>();

    @OneToMany(mappedBy = "caseApplication", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<ApplicationRetentionEntity> applicationRetentions = new ArrayList<>();

    @OneToMany(mappedBy = "caseApplication", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<ApplicationStatementOfTruthEntity> statementsOfTruth = new ArrayList<>();

    @OneToMany(mappedBy = "caseApplication", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<ApplicationStatusEntity> applicationStatuses = new ArrayList<>();

    @OneToMany(mappedBy = "caseApplication", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseEvidenceEntity> evidence = new ArrayList<>();

    @OneToMany(mappedBy = "caseApplication", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseHearingEntity> hearings = new ArrayList<>();

    @OneToMany(mappedBy = "caseApplication", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseMediationEntity> mediations = new ArrayList<>();

    @OneToMany(mappedBy = "caseApplication", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<DocumentEntity> documents = new ArrayList<>();

    @OneToMany(mappedBy = "caseApplication", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<MarketRentCaseEntity> marketRentCases = new ArrayList<>();

    @OneToMany(mappedBy = "caseApplication", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<NonRentCaseEntity> nonRentCases = new ArrayList<>();
}
