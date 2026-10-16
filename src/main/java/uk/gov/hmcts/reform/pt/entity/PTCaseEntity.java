package uk.gov.hmcts.reform.pt.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uk.gov.hmcts.reform.pt.ccd.domain.LandlordRepresentativeType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pt_case")
public class PTCaseEntity extends AuditableEntity {
    @Column(nullable = false, unique = true)
    private Long caseReference;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private LandlordRepresentativeType landlordType;

    private LocalDateTime applicationSubmittedDate;
    private LocalDateTime caseIssuedDate;

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<AddressEntity> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CaseNotificationEntity> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @OrderBy("id")
    @Builder.Default
    private List<DocumentEntity> documents = new ArrayList<>();

    @OneToOne(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    private MarketRentCaseEntity marketRentCase;

    @OneToMany(mappedBy = "ptCase", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<CasePartyEntity> parties = new ArrayList<>();

}
