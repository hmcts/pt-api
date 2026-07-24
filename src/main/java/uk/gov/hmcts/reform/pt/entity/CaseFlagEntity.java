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
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "case_flag")
public class CaseFlagEntity extends AuditableEntity {
    @Column(length = 100)
    private String flagCode;

    @Column(length = 100)
    private String subTypeKey;

    @Column(length = 100)
    private String subTypeValue;

    @Column(length = 100)
    private String subTypeValueCy;

    @Column(length = 100)
    private String otherDescription;

    @Column(length = 100)
    private String otherDescriptionCy;

    @Column(length = 100)
    private String flagComment;

    @Column(length = 100)
    private String flagCommentCy;

    @Column(length = 100)
    private String flagUpdateComment;

    @Column(length = 100)
    private String flagUpdateCommentCy;

    @Column(length = 100)
    private String flagPath;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pt_case_id")
    @JsonBackReference
    private PTCaseEntity ptCase;
}
