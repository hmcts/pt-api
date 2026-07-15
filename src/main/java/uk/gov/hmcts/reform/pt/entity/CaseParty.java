package uk.gov.hmcts.reform.pt.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

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
@Table(name = "case_party")
public class CaseParty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String firstName;

    @Column(length = 100)
    private String lastName;

    @Column(length = 100)
    private String organisationName;

    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 20)
    private String mobilePhoneNumber;

    @Column(length = 100)
    private String emailAddress;

    @Column(length = 100)
    private LocalDateTime dateOfBirth;

    private Integer referenceNumber;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime lastModifiedDate;

    @Column(length = 100)
    private String lastModifiedBy;

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CasePartyAccess> access = new ArrayList<>();

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CasePartyAddress> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CasePartyAttribute> attributeAssertions = new ArrayList<>();

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CasePartyContactPreference> contactPreferences = new ArrayList<>();

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CasePartyEvent> events = new ArrayList<>();

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CasePartyFlag> flags = new ArrayList<>();

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CasePartyRepresentative> representatives = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "case_party_role_id")
    @JsonBackReference
    private CasePartyRole role;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "case_party_type_id")
    @JsonBackReference
    private CasePartyType type;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "pt_case_id")
    @JsonBackReference
    private PTCaseEntity ptCase;
}
