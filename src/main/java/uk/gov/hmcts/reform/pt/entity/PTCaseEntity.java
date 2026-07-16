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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pt_case")
public class PTCaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long caseReference;

    @Column(length = 100)
    private String legislativeCountry;

    @Column(length = 100)
    private String status;

    @Column(length = 100)
    private String landlordType;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime lastModifiedDate;

    @Column(length = 100)
    private String lastModifiedBy;

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseTaskEntity> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseNoteEntity> notes = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CasePropertyEntity> properties = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseEventEntity> events = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseStateEntity> states = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseFlagEntity> flags = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<ApplicationStatementOfTruthEntity> statementsOfTruth = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseEvidenceEntity> evidence = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseHearingEntity> hearings = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseMediationEntity> mediations = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseNotificationEntity> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseOrderEntity> orders = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<DocumentEntity> documents = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<MarketRentCaseEntity> marketRentCases = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<NonRentCaseEntity> nonRentCases = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<NoticeOfRentChangeEntity> noticeOfRentChanges = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<TenancyDetailsEntity> tenancyDetails = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CasePartyEntity> parties = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_type_id")
    private CaseTypeEntity caseType;
}
