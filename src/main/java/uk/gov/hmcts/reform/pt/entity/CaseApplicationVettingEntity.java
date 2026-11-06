package uk.gov.hmcts.reform.pt.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "case_application_vetting")
public class CaseApplicationVettingEntity extends AuditableEntity {
    @Column(length = 100)
    private String vettingStatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_rent_case_id", unique = true)
    @JsonBackReference
    private MarketRentCaseEntity marketRentCase;

    @OneToOne(mappedBy = "caseApplicationVetting", cascade = CascadeType.ALL)
    @JsonManagedReference
    private MissingInformationRequestEntity missingInformationRequest;
}
