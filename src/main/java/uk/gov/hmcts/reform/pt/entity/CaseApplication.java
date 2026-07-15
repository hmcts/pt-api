package uk.gov.hmcts.reform.pt.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "case_application")
public class CaseApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime submittedDate;
    private LocalDateTime issuedDate;

    @Column(length = 100)
    private String status;

    @Column(length = 100)
    private String language;

    @Column(length = 100)
    private String dxNumber;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime lastModifiedDate;

    @Column(length = 100)
    private String lastModifiedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_type_id")
    private CaseType caseType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_party_id")
    private CaseParty caseParty;

    @OneToMany(mappedBy = "caseApplication", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<ApplicationEvent> applicationEvents = new ArrayList<>();

    @OneToMany(mappedBy = "caseApplication", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<ApplicationFee> applicationFees = new ArrayList<>();

    @OneToMany(mappedBy = "caseApplication", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<ApplicationRetention> applicationRetentions = new ArrayList<>();

    @OneToMany(mappedBy = "caseApplication", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<ApplicationStatementOfTruth> statementsOfTruth = new ArrayList<>();

    @OneToMany(mappedBy = "caseApplication", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<ApplicationStatus> applicationStatuses = new ArrayList<>();

    @OneToMany(mappedBy = "caseApplication", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseEvidence> evidence = new ArrayList<>();

    @OneToMany(mappedBy = "caseApplication", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseHearing> hearings = new ArrayList<>();

    @OneToMany(mappedBy = "caseApplication", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseMediation> mediations = new ArrayList<>();

    @OneToMany(mappedBy = "caseApplication", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<Document> documents = new ArrayList<>();

    @OneToMany(mappedBy = "caseApplication", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<MarketRentCase> marketRentCases = new ArrayList<>();

    @OneToMany(mappedBy = "caseApplication", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<NonRentCase> nonRentCases = new ArrayList<>();
}
