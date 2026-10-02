package uk.gov.hmcts.reform.pt.notify.task;

import com.github.kagkarlsson.scheduler.task.CompletionHandler;
import com.github.kagkarlsson.scheduler.task.ExecutionContext;
import com.github.kagkarlsson.scheduler.task.TaskInstance;
import com.github.kagkarlsson.scheduler.task.helper.CustomTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pt.entity.CaseNotificationEntity;
import uk.gov.hmcts.reform.pt.notify.exception.NotificationException;
import uk.gov.hmcts.reform.pt.notify.model.NotificationStatus;
import uk.gov.hmcts.reform.pt.notify.model.SendEmailTaskData;
import uk.gov.hmcts.reform.pt.notify.service.NotificationService;
import uk.gov.hmcts.reform.pt.repository.CaseNotificationRepository;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SendEmailTaskComponentTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private CaseNotificationRepository caseNotificationRepository;

    @Mock
    private ExecutionContext executionContext;

    private SendEmailTaskComponent component;
    private CustomTask<SendEmailTaskData> task;

    private static final int MAX_RETRIES = 3;
    private static final Duration BACKOFF_DELAY = Duration.ofSeconds(10);
    private static final Duration TASK_DELAY = Duration.ofSeconds(30);

    @BeforeEach
    void setUp() {
        component = new SendEmailTaskComponent(
            notificationService,
            notificationClient,
            caseNotificationRepository,
            MAX_RETRIES,
            BACKOFF_DELAY,
            TASK_DELAY
        );
        task = component.sendEmailTask();
    }

    @Test
    @DisplayName("Should return OnCompleteRemove when notification entity is not found in database")
    void executeNotificationNotFoundReturnOnCompleteRemove() throws Exception {
        SendEmailTaskData taskData = SendEmailTaskData.builder()
            .taskId("task-1")
            .dbNotificationId(100L)
            .build();
        TaskInstance<SendEmailTaskData> taskInstance = new TaskInstance<>(
            SendEmailTaskComponent.sendEmailTask.getTaskName(),
            "task-1",
            taskData
        );

        when(caseNotificationRepository.findById(100L)).thenReturn(Optional.empty());

        CompletionHandler<SendEmailTaskData> handler = task.execute(taskInstance, executionContext);

        assertThat(handler).isInstanceOf(CompletionHandler.OnCompleteRemove.class);
        verify(notificationClient, never()).sendEmail(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should send email successfully and return OnCompleteReplace with verify-email-task")
    void executeEmailSentSuccessfullyReturnOnCompleteReplace() throws Exception {
        UUID providerNotificationId = UUID.randomUUID();
        SendEmailTaskData taskData = SendEmailTaskData.builder()
            .taskId("task-1")
            .dbNotificationId(100L)
            .emailAddress("test@example.com")
            .templateId("template-123")
            .personalisation(Map.of("key", "val"))
            .build();
        TaskInstance<SendEmailTaskData> taskInstance = new TaskInstance<>(
            SendEmailTaskComponent.sendEmailTask.getTaskName(),
            "task-1",
            taskData
        );

        CaseNotificationEntity notification = CaseNotificationEntity.builder().id(100L).build();
        when(caseNotificationRepository.findById(100L)).thenReturn(Optional.of(notification));

        SendEmailResponse response = mock(SendEmailResponse.class);
        when(response.getNotificationId()).thenReturn(providerNotificationId);
        when(notificationClient.sendEmail(
            eq("template-123"),
            eq("test@example.com"),
            eq(Map.of("key", "val")),
            anyString()
        )).thenReturn(response);

        CompletionHandler<SendEmailTaskData> handler = task.execute(taskInstance, executionContext);

        assertThat(handler).isInstanceOf(CompletionHandler.OnCompleteReplace.class);
        verify(notificationService).updateNotificationAfterSending(100L, providerNotificationId);
    }

    @Test
    @DisplayName("Should throw NotificationException when email service returns null notification ID")
    void executeNotificationIdIsNullNotificationException() throws Exception {
        SendEmailTaskData taskData = SendEmailTaskData.builder()
            .taskId("task-1")
            .dbNotificationId(100L)
            .emailAddress("test@example.com")
            .templateId("template-123")
            .build();
        TaskInstance<SendEmailTaskData> taskInstance = new TaskInstance<>(
            SendEmailTaskComponent.sendEmailTask.getTaskName(),
            "task-1",
            taskData
        );

        CaseNotificationEntity notification = CaseNotificationEntity.builder().id(100L).build();
        when(caseNotificationRepository.findById(100L)).thenReturn(Optional.of(notification));

        SendEmailResponse response = mock(SendEmailResponse.class);
        when(response.getNotificationId()).thenReturn(null);
        when(notificationClient.sendEmail(any(), any(), any(), any())).thenReturn(response);

        assertThatThrownBy(() -> task.execute(taskInstance, executionContext))
            .isInstanceOf(NotificationException.class)
            .hasMessage("Null notification ID from email service");
    }

    @Test
    @DisplayName("Should handle 400 Bad Request permanent failure by updating status and returning OnCompleteRemove")
    void executePermanentFailure400UpdateStatusAndRemove() throws Exception {
        SendEmailTaskData taskData = SendEmailTaskData.builder()
            .taskId("task-1")
            .dbNotificationId(100L)
            .emailAddress("test@example.com")
            .templateId("template-123")
            .build();
        TaskInstance<SendEmailTaskData> taskInstance = new TaskInstance<>(
            SendEmailTaskComponent.sendEmailTask.getTaskName(),
            "task-1",
            taskData
        );

        CaseNotificationEntity notification = CaseNotificationEntity.builder().id(100L).build();
        when(caseNotificationRepository.findById(100L)).thenReturn(Optional.of(notification));

        NotificationClientException clientException = mock(NotificationClientException.class);
        when(clientException.getHttpResult()).thenReturn(400);
        when(clientException.getMessage()).thenReturn("Bad Request");
        when(notificationClient.sendEmail(any(), any(), any(), any())).thenThrow(clientException);

        CompletionHandler<SendEmailTaskData> handler = task.execute(taskInstance, executionContext);

        assertThat(handler).isInstanceOf(CompletionHandler.OnCompleteRemove.class);
        verify(notificationService).updateNotificationStatus(100L, NotificationStatus.PERMANENT_FAILURE.toString());
    }

    @Test
    @DisplayName("Should handle 403 Forbidden permanent failure by updating status and returning OnCompleteRemove")
    void executePermanentFailure403UpdateStatusAndRemove() throws Exception {
        SendEmailTaskData taskData = SendEmailTaskData.builder()
            .taskId("task-1")
            .dbNotificationId(100L)
            .emailAddress("test@example.com")
            .templateId("template-123")
            .build();
        TaskInstance<SendEmailTaskData> taskInstance = new TaskInstance<>(
            SendEmailTaskComponent.sendEmailTask.getTaskName(),
            "task-1",
            taskData
        );

        CaseNotificationEntity notification = CaseNotificationEntity.builder().id(100L).build();
        when(caseNotificationRepository.findById(100L)).thenReturn(Optional.of(notification));

        NotificationClientException clientException = mock(NotificationClientException.class);
        when(clientException.getHttpResult()).thenReturn(403);
        when(clientException.getMessage()).thenReturn("Forbidden");
        when(notificationClient.sendEmail(any(), any(), any(), any())).thenThrow(clientException);

        CompletionHandler<SendEmailTaskData> handler = task.execute(taskInstance, executionContext);

        assertThat(handler).isInstanceOf(CompletionHandler.OnCompleteRemove.class);
        verify(notificationService).updateNotificationStatus(100L, NotificationStatus.PERMANENT_FAILURE.toString());
    }

    @Test
    @DisplayName("Should throw NotificationException on temporary failure (e.g. 500)")
    void executeTemporaryFailureThrowNotificationException() throws Exception {
        SendEmailTaskData taskData = SendEmailTaskData.builder()
            .taskId("task-1")
            .dbNotificationId(100L)
            .emailAddress("test@example.com")
            .templateId("template-123")
            .build();
        TaskInstance<SendEmailTaskData> taskInstance = new TaskInstance<>(
            SendEmailTaskComponent.sendEmailTask.getTaskName(),
            "task-1",
            taskData
        );

        CaseNotificationEntity notification = CaseNotificationEntity.builder().id(100L).build();
        when(caseNotificationRepository.findById(100L)).thenReturn(Optional.of(notification));

        NotificationClientException clientException = mock(NotificationClientException.class);
        when(clientException.getHttpResult()).thenReturn(500);
        when(clientException.getMessage()).thenReturn("Internal Server Error");
        when(notificationClient.sendEmail(any(), any(), any(), any())).thenThrow(clientException);

        assertThatThrownBy(() -> task.execute(taskInstance, executionContext))
            .isInstanceOf(NotificationException.class)
            .hasMessage("Email temporarily failed to send.")
            .hasCause(clientException);
    }
}
