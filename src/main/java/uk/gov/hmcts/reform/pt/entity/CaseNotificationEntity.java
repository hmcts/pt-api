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
import uk.gov.hmcts.reform.pt.notify.model.NotificationStatus;
import uk.gov.hmcts.reform.pt.notify.model.NotificationType;

import java.time.Instant;
import java.util.UUID;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "case_notification")
public class CaseNotificationEntity extends AuditableEntity {
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private NotificationStatus status;

    @Column(length = 100, nullable = false)
    private String recipient;

    private UUID providerNotificationId;

    private Instant scheduledDate;
    private Instant submittedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pt_case_id")
    @JsonBackReference
    private PTCaseEntity ptCase;
}
