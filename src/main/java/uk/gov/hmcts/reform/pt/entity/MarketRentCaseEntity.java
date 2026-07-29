package uk.gov.hmcts.reform.pt.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_application_id")
    @JsonBackReference
    private CaseApplicationEntity caseApplication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pt_case_id")
    @JsonBackReference
    private PTCaseEntity ptCase;
}
