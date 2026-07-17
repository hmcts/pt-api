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
@Table(name = "case_hearing")
public class CaseHearingEntity extends AuditableEntity {
    @Column(length = 100)
    private String hearingType;

    private LocalDateTime hearingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pt_case_id")
    @JsonBackReference
    private PTCaseEntity ptCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_application_id")
    @JsonBackReference
    private CaseApplicationEntity caseApplication;

    @OneToMany(mappedBy = "caseHearing", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<HearingDecisionEntity> hearingDecisions = new ArrayList<>();

    @OneToMany(mappedBy = "caseHearing", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<HearingInspectionEntity> hearingInspections = new ArrayList<>();
}
