package uk.gov.hmcts.reform.pt.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notice_of_rent_change")
public class NoticeOfRentChangeEntity extends AuditableEntity {
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private YesOrNo documentUploaded;

    @Column(length = 100)
    private String reasonDocumentNotUploaded;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private YesOrNo hasNoticeOfIncreaseValidityChallenge;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private YesOrNo noticeOfIncreaseValidityChallengeDocumentUploaded;

    @Column(length = 100)
    private String reasonNoticeOfIncreaseValidityDocumentNotUploaded;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pt_case_id")
    @JsonBackReference
    private PTCaseEntity ptCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private DocumentEntity document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_of_increase_validity_challenge_document_id")
    private DocumentEntity noticeOfIncreaseValidityChallengeDocument;
}
