package uk.gov.hmcts.reform.pt.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hearing_decision")
public class HearingDecisionEntity extends AuditableEntity {
    @Column(length = 100)
    private String description;

    private LocalDateTime decisionDate;

    @OneToMany(mappedBy = "hearingDecision", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<DecisionAppealEntity> decisionAppeals = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_hearing_id")
    @JsonBackReference
    private CaseHearingEntity caseHearing;
}
