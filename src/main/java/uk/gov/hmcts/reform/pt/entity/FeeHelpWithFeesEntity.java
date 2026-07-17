package uk.gov.hmcts.reform.pt.entity;

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
@Table(name = "fee_help_with_fees")
public class FeeHelpWithFeesEntity extends AuditableEntity {
    private Integer referenceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_fee_id")
    private ApplicationFeeEntity applicationFee;
}
