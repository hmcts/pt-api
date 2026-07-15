package uk.gov.hmcts.reform.pt.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
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

    private Long caseReference;

    @Column(length = 100)
    private String legislativeCountry;

    @Column(length = 100)
    private String status;

    @Column(length = 100)
    private String landlordType;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime lastModifiedDate;

    @Column(length = 100)
    private String lastModifiedBy;

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseTask> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseNote> notes = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseProperty> properties = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseEvent> events = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseState> states = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseFlag> flags = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<ApplicationStatementOfTruth> statementsOfTruth = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseEvidence> evidence = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseHearing> hearings = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseMediation> mediations = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseNotification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseOrder> orders = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<Document> documents = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<MarketRentCase> marketRentCases = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<NonRentCase> nonRentCases = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<NoticeOfRentChange> noticeOfRentChanges = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<TenancyDetails> tenancyDetails = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseParty> parties = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "case_type_id")
    @JsonBackReference
    private CaseType caseType;
}
