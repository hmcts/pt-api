package uk.gov.hmcts.reform.pt.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
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

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "application_statement_of_truth")
public class ApplicationStatementOfTruthEntity extends AuditableEntity {
    private LocalDateTime completedDate;

    @Column(length = 100)
    private String completedBy;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private YesOrNo accepted;

    @Column(length = 100)
    private String fullName;

    @Column(length = 100)
    private String firmName;

    @Column(length = 100)
    private String positionHeld;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_rent_case_id", unique = true)
    @JsonBackReference
    private MarketRentCaseEntity marketRentCase;
}
