package uk.gov.hmcts.reform.pt.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
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
@Table(name = "decision_appeal")
public class DecisionAppealEntity extends AuditableEntity {
    @Column(length = 100)
    private String appealType;

    @Column(length = 100)
    private String description;

    @Column(length = 100)
    private String permission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hearing_decision_id")
    @JsonBackReference
    private HearingDecisionEntity hearingDecision;
}
