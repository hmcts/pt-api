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
import uk.gov.hmcts.reform.pt.entity.reference.ActualCancellationReasonEntity;
import uk.gov.hmcts.reform.pt.entity.reference.ActualPartHeardReasonEntity;
import uk.gov.hmcts.reform.pt.entity.reference.AutoListChangeReasonsEntity;
import uk.gov.hmcts.reform.pt.entity.reference.CaseManagementCancellationReasonEntity;
import uk.gov.hmcts.reform.pt.entity.reference.ChangeReasonEntity;
import uk.gov.hmcts.reform.pt.entity.reference.HearingChannelEntity;
import uk.gov.hmcts.reform.pt.entity.reference.HearingPriorityEntity;
import uk.gov.hmcts.reform.pt.entity.reference.HearingTypeEntity;
import uk.gov.hmcts.reform.pt.entity.reference.HearingVenueEntity;
import uk.gov.hmcts.reform.pt.entity.reference.ListingStatusEntity;

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
    @JoinColumn(name = "hearing_channel_key")
    private HearingChannelEntity hearingChannel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hearing_priority_key")
    private HearingPriorityEntity hearingPriority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hearing_type_key")
    private HearingTypeEntity hearingTypeEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_management_cancellation_reason_key")
    private CaseManagementCancellationReasonEntity caseManagementCancellationReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actual_cancellation_reason_key")
    private ActualCancellationReasonEntity actualCancellationReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actual_part_heard_reason_key")
    private ActualPartHeardReasonEntity actualPartHeardReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hearing_judge_id")
    private HearingJudgeEntity hearingJudge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "change_reason_key")
    private ChangeReasonEntity changeReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_status_key")
    private ListingStatusEntity listingStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auto_list_change_reasons_key")
    private AutoListChangeReasonsEntity autoListChangeReasons;

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

    @OneToMany(mappedBy = "caseHearing", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<HearingJudgeEntity> hearingJudges = new ArrayList<>();

    @OneToMany(mappedBy = "caseHearing", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<HearingPartyEntity> hearingParties = new ArrayList<>();

    @OneToMany(mappedBy = "caseHearing", cascade = CascadeType.ALL)
    @JsonManagedReference
    @Builder.Default
    private List<HearingVenueEntity> hearingVenues = new ArrayList<>();
}
