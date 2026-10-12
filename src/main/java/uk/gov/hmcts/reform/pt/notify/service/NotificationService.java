package uk.gov.hmcts.reform.pt.notify.service;

import com.github.kagkarlsson.scheduler.SchedulerClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.entity.CaseNotificationEntity;
import uk.gov.hmcts.reform.pt.notify.config.GovNotifyConfiguration;
import uk.gov.hmcts.reform.pt.notify.exception.NotificationException;
import uk.gov.hmcts.reform.pt.notify.model.NotificationRequest;
import uk.gov.hmcts.reform.pt.notify.model.NotificationResponse;
import uk.gov.hmcts.reform.pt.notify.model.NotificationStatus;
import uk.gov.hmcts.reform.pt.notify.model.NotificationType;
import uk.gov.hmcts.reform.pt.notify.model.SendEmailTaskData;
import uk.gov.hmcts.reform.pt.notify.task.SendEmailTaskComponent;
import uk.gov.hmcts.reform.pt.repository.CaseNotificationRepository;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final GovNotifyConfiguration govNotifyConfiguration;
    private final CaseNotificationRepository caseNotificationRepository;
    private final SchedulerClient schedulerClient;

    @Transactional
    public NotificationResponse scheduleEmailNotification(NotificationRequest request) {
        String taskId = UUID.randomUUID().toString();

        CaseNotificationEntity caseNotification = createCaseNotification(request, taskId);
        SendEmailTaskData taskData = SendEmailTaskData.builder()
            .taskId(taskId)
            .emailAddress(request.getEmailAddress())
            .templateId(govNotifyConfiguration.getTemplateId(request.getTemplate()))
            .personalisation(request.getPersonalisation())
            .emailReplyToId(request.getEmailReplyToId())
            .dbNotificationId(caseNotification.getId())
            .build();

        boolean scheduled = schedulerClient.scheduleIfNotExists(
            SendEmailTaskComponent.sendEmailTask
                .instance(taskId)
                .data(taskData)
                .scheduledTo(Instant.now())
        );

        if (!scheduled) {
            log.warn("Task with ID {} already exists", taskId);
        }

        NotificationResponse response = NotificationResponse.builder()
            .taskId(taskId)
            .status(NotificationStatus.SCHEDULED.toString())
            .notificationId(caseNotification.getId())
            .build();

        log.info("Email notification scheduled with task ID: {} and notification ID: {}",
                 taskId, caseNotification.getId());

        return response;
    }

    @Transactional
    public CaseNotificationEntity createCaseNotification(NotificationRequest request, String taskId) {
        CaseNotificationEntity notification = CaseNotificationEntity.builder()
            .ptCase(request.getPtCase())
            .type(NotificationType.EMAIL)
            .status(NotificationStatus.PENDING_SCHEDULE)
            .recipient(request.getEmailAddress())
            .build();

        try {
            notification = caseNotificationRepository.save(notification);
            log.info(
                "Case Notification with ID {} has been saved to the database with task ID {}",
                notification.getId(), taskId
            );
            return notification;
        } catch (DataAccessException dataAccessException) {
            log.error(
                "Failed to save Case Notification with Case ID: {}. Reason: {}",
                notification.getPtCase().getId(),
                dataAccessException.getMessage(),
                dataAccessException
            );
            throw new NotificationException("Failed to save Case Notification.", dataAccessException);
        }
    }

    @Transactional
    public void updateNotificationStatus(CaseNotificationEntity notification,
                                         UUID providerNotificationId,
                                         NotificationStatus status) {
        try {
            notification.setStatus(status);

            if (providerNotificationId != null) {
                notification.setProviderNotificationId(providerNotificationId);
            }

            Instant now = Instant.now();
            switch (status) {
                case SUBMITTED -> notification.setSubmittedDate(now);
                case SCHEDULED -> notification.setScheduledDate(now);
            }

            caseNotificationRepository.save(notification);
            log.info("Updated notification status to {} for notification ID: {}",
                     status, notification.getId());
        } catch (Exception e) {
            log.error("Error updating notification status to {}: {}",
                      status, e.getMessage(), e);
        }
    }

    @Transactional
    public void updateNotificationStatus(Long dbNotificationId, String statusString) {
        caseNotificationRepository.findById(dbNotificationId).ifPresentOrElse(
            notification -> {
                try {
                    NotificationStatus status = NotificationStatus.fromString(statusString);
                    updateNotificationStatus(notification, null, status);
                } catch (IllegalArgumentException e) {
                    log.warn("Unknown notification status: {}", statusString);
                }
            },
            () -> log.error("Notification not found with ID on status update: {}", dbNotificationId)
        );
    }

    @Transactional
    public void updateNotificationAfterSending(Long dbNotificationId, UUID providerNotificationId) {
        caseNotificationRepository.findById(dbNotificationId).ifPresentOrElse(
            notification ->
                updateNotificationStatus(notification, providerNotificationId, NotificationStatus.SUBMITTED),
            () -> log.error("Notification not found with ID: {}", dbNotificationId)
        );
    }
}
