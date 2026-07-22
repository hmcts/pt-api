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

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hearing_inspection")
public class HearingInspectionEntity extends AuditableEntity {
    @Column(length = 100)
    private String description;

    private LocalDateTime inspectionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_hearing_id")
    @JsonBackReference
    private CaseHearingEntity caseHearing;
}
