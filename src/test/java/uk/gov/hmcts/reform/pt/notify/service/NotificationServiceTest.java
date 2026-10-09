package uk.gov.hmcts.reform.pt.notify.service;

import com.github.kagkarlsson.scheduler.SchedulerClient;
import com.github.kagkarlsson.scheduler.task.SchedulableInstance;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.CannotAcquireLockException;
import uk.gov.hmcts.reform.pt.entity.CaseNotificationEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.notify.config.GovNotifyConfiguration;
import uk.gov.hmcts.reform.pt.notify.exception.NotificationException;
import uk.gov.hmcts.reform.pt.notify.model.NotificationRequest;
import uk.gov.hmcts.reform.pt.notify.model.NotificationResponse;
import uk.gov.hmcts.reform.pt.notify.model.NotificationStatus;
import uk.gov.hmcts.reform.pt.notify.model.NotificationType;
import uk.gov.hmcts.reform.pt.notify.model.SendEmailTaskData;
import uk.gov.hmcts.reform.pt.notify.template.EmailTemplate;
import uk.gov.hmcts.reform.pt.repository.CaseNotificationRepository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private GovNotifyConfiguration govNotifyConfiguration;

    @Mock
    private CaseNotificationRepository caseNotificationRepository;

    @Mock
    private SchedulerClient schedulerClient;

    @InjectMocks
    private NotificationService notificationService;

    @Captor
    private ArgumentCaptor<CaseNotificationEntity> notificationEntityCaptor;

    @Captor
    private ArgumentCaptor<SchedulableInstance<SendEmailTaskData>> schedulableInstanceCaptor;

    @Test
    @DisplayName("Should successfully schedule email notification when task does not exist")
    void scheduleEmailNotificationSuccess() {
        PTCaseEntity ptCase = PTCaseEntity.builder().id(1L).build();
        NotificationRequest request = NotificationRequest.builder()
            .ptCase(ptCase)
            .emailAddress("user@example.com")
            .template(EmailTemplate.TEST_TEMPLATE)
            .personalisation(Map.of("name", "John"))
            .emailReplyToId("reply-to-id")
            .build();

        CaseNotificationEntity savedNotification = CaseNotificationEntity.builder()
            .id(10L)
            .ptCase(ptCase)
            .type(NotificationType.EMAIL)
            .status(NotificationStatus.PENDING_SCHEDULE)
            .recipient("user@example.com")
            .build();

        when(caseNotificationRepository.save(any(CaseNotificationEntity.class))).thenReturn(savedNotification);
        when(govNotifyConfiguration.getTemplateId(EmailTemplate.TEST_TEMPLATE)).thenReturn("gov-notify-template-123");
        when(schedulerClient.scheduleIfNotExists(any())).thenReturn(true);

        NotificationResponse response = notificationService.scheduleEmailNotification(request);

        assertThat(response).isNotNull();
        assertThat(response.getNotificationId()).isEqualTo(10L);
        assertThat(response.getStatus()).isEqualTo(NotificationStatus.SCHEDULED.toString());
        assertThat(response.getTaskId()).isNotBlank();

        verify(caseNotificationRepository).save(notificationEntityCaptor.capture());
        CaseNotificationEntity captured = notificationEntityCaptor.getValue();
        assertThat(captured.getPtCase()).isEqualTo(ptCase);
        assertThat(captured.getType()).isEqualTo(NotificationType.EMAIL);
        assertThat(captured.getStatus()).isEqualTo(NotificationStatus.PENDING_SCHEDULE);
        assertThat(captured.getRecipient()).isEqualTo("user@example.com");

        verify(schedulerClient).scheduleIfNotExists(schedulableInstanceCaptor.capture());
        SchedulableInstance<SendEmailTaskData> instance = schedulableInstanceCaptor.getValue();
        assertThat(instance.getTaskInstance().getData().getTemplateId()).isEqualTo("gov-notify-template-123");
        assertThat(instance.getTaskInstance().getData().getEmailAddress()).isEqualTo("user@example.com");
        assertThat(instance.getTaskInstance().getData().getDbNotificationId()).isEqualTo(10L);
        assertThat(instance.getTaskInstance().getData().getPersonalisation()).isEqualTo(Map.of("name", "John"));
        assertThat(instance.getTaskInstance().getData().getEmailReplyToId()).isEqualTo("reply-to-id");
    }

    @Test
    @DisplayName("Should handle case when task already exists in scheduler")
    void scheduleEmailNotificationTaskAlreadyExists() {
        PTCaseEntity ptCase = PTCaseEntity.builder().id(1L).build();
        NotificationRequest request = NotificationRequest.builder()
            .ptCase(ptCase)
            .emailAddress("user@example.com")
            .template(EmailTemplate.TEST_TEMPLATE)
            .build();

        CaseNotificationEntity savedNotification = CaseNotificationEntity.builder()
            .id(10L)
            .ptCase(ptCase)
            .type(NotificationType.EMAIL)
            .status(NotificationStatus.PENDING_SCHEDULE)
            .recipient("user@example.com")
            .build();

        when(caseNotificationRepository.save(any(CaseNotificationEntity.class))).thenReturn(savedNotification);
        when(govNotifyConfiguration.getTemplateId(EmailTemplate.TEST_TEMPLATE)).thenReturn("gov-notify-template-123");
        when(schedulerClient.scheduleIfNotExists(any())).thenReturn(false);

        NotificationResponse response = notificationService.scheduleEmailNotification(request);

        assertThat(response).isNotNull();
        assertThat(response.getNotificationId()).isEqualTo(10L);
        assertThat(response.getStatus()).isEqualTo(NotificationStatus.SCHEDULED.toString());
    }

    @Test
    @DisplayName("Should throw NotificationException when DataAccessException occurs during save")
    void createCaseNotificationDataAccessException() {
        PTCaseEntity ptCase = PTCaseEntity.builder().id(1L).build();
        NotificationRequest request = NotificationRequest.builder()
            .ptCase(ptCase)
            .emailAddress("user@example.com")
            .build();

        CannotAcquireLockException dataAccessException = new CannotAcquireLockException("Database lock error");
        when(caseNotificationRepository.save(any(CaseNotificationEntity.class)))
            .thenThrow(dataAccessException);

        assertThatThrownBy(() -> notificationService.createCaseNotification(request, "task-123"))
            .isInstanceOf(NotificationException.class)
            .hasMessage("Failed to save Case Notification.")
            .hasCause(dataAccessException);
    }

    @Test
    @DisplayName("Should update notification status to SUBMITTED and set provider ID and submitted date")
    void updateNotificationStatusSubmitted() {
        CaseNotificationEntity notification = CaseNotificationEntity.builder()
            .id(10L)
            .status(NotificationStatus.PENDING_SCHEDULE)
            .build();
        UUID providerId = UUID.randomUUID();

        notificationService.updateNotificationStatus(notification, providerId, NotificationStatus.SUBMITTED);

        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.SUBMITTED);
        assertThat(notification.getProviderNotificationId()).isEqualTo(providerId);
        assertThat(notification.getSubmittedDate()).isNotNull();
        verify(caseNotificationRepository).save(notification);
    }

    @Test
    @DisplayName("Should update notification status to SCHEDULED and set scheduled date")
    void updateNotificationStatusScheduled() {
        CaseNotificationEntity notification = CaseNotificationEntity.builder()
            .id(10L)
            .status(NotificationStatus.PENDING_SCHEDULE)
            .build();

        notificationService.updateNotificationStatus(notification, null, NotificationStatus.SCHEDULED);

        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.SCHEDULED);
        assertThat(notification.getScheduledDate()).isNotNull();
        verify(caseNotificationRepository).save(notification);
    }

    @Test
    @DisplayName("Should update notification status to DELIVERED without changing dates")
    void updateNotificationStatusDelivered() {
        CaseNotificationEntity notification = CaseNotificationEntity.builder()
            .id(10L)
            .status(NotificationStatus.SUBMITTED)
            .build();

        notificationService.updateNotificationStatus(notification, null, NotificationStatus.DELIVERED);

        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.DELIVERED);
        verify(caseNotificationRepository).save(notification);
    }

    @Test
    @DisplayName("Should catch and handle exception gracefully when save fails during status update")
    void updateNotificationStatusException() {
        CaseNotificationEntity notification = CaseNotificationEntity.builder()
            .id(10L)
            .status(NotificationStatus.PENDING_SCHEDULE)
            .build();

        when(caseNotificationRepository.save(any())).thenThrow(new RuntimeException("DB error"));

        notificationService.updateNotificationStatus(notification, null, NotificationStatus.DELIVERED);

        verify(caseNotificationRepository).save(notification);
    }

    @Test
    @DisplayName("Should update notification after sending when entity is found")
    void updateNotificationAfterSendingFound() {
        CaseNotificationEntity notification = CaseNotificationEntity.builder()
            .id(10L)
            .status(NotificationStatus.PENDING_SCHEDULE)
            .build();
        UUID providerId = UUID.randomUUID();

        when(caseNotificationRepository.findById(10L)).thenReturn(Optional.of(notification));

        notificationService.updateNotificationAfterSending(10L, providerId);

        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.SUBMITTED);
        assertThat(notification.getProviderNotificationId()).isEqualTo(providerId);
        verify(caseNotificationRepository).save(notification);
    }

    @Test
    @DisplayName("Should log error and not fail when entity not found in updateNotificationAfterSending")
    void updateNotificationAfterSendingEntityNotFound() {
        when(caseNotificationRepository.findById(10L)).thenReturn(Optional.empty());

        notificationService.updateNotificationAfterSending(10L, UUID.randomUUID());

        verify(caseNotificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update status from string when entity found and status string is valid")
    void updateNotificationStatusByStringValidStatus() {
        CaseNotificationEntity notification = CaseNotificationEntity.builder()
            .id(10L)
            .status(NotificationStatus.SUBMITTED)
            .build();

        when(caseNotificationRepository.findById(10L)).thenReturn(Optional.of(notification));

        notificationService.updateNotificationStatus(10L, "delivered");

        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.DELIVERED);
        verify(caseNotificationRepository).save(notification);
    }

    @Test
    @DisplayName("Should log warning and not update status when status string is invalid")
    void updateNotificationStatusByStringInvalidStatus() {
        CaseNotificationEntity notification = CaseNotificationEntity.builder()
            .id(10L)
            .status(NotificationStatus.SUBMITTED)
            .build();

        when(caseNotificationRepository.findById(10L)).thenReturn(Optional.of(notification));

        notificationService.updateNotificationStatus(10L, "unknown-status");

        verify(caseNotificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should log error when entity not found on status update by string")
    void updateNotificationStatusByStringNotFound() {
        when(caseNotificationRepository.findById(10L)).thenReturn(Optional.empty());

        notificationService.updateNotificationStatus(10L, "delivered");

        verify(caseNotificationRepository, never()).save(any());
    }
}
