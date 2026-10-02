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
import uk.gov.hmcts.reform.pt.notify.exception.NotificationException;
import uk.gov.hmcts.reform.pt.notify.model.NotificationStatus;
import uk.gov.hmcts.reform.pt.notify.model.SendEmailTaskData;
import uk.gov.hmcts.reform.pt.notify.service.NotificationService;
import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifyEmailTaskComponentTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private ExecutionContext executionContext;

    private VerifyEmailTaskComponent component;
    private CustomTask<SendEmailTaskData> task;

    private static final int MAX_RETRIES = 5;
    private static final Duration BACKOFF_DELAY = Duration.ofSeconds(15);

    @BeforeEach
    void setUp() {
        component = new VerifyEmailTaskComponent(
            notificationService,
            notificationClient,
            MAX_RETRIES,
            BACKOFF_DELAY
        );
        task = component.verifyEmailTask();
    }

    @Test
    @DisplayName("Should update status to delivered when notification status is delivered")
    void executeStatusIsDeliveredUpdateStatusAndRemove() throws Exception {
        String notificationId = "notif-uuid-123";
        SendEmailTaskData taskData = SendEmailTaskData.builder()
            .taskId("task-1")
            .notificationId(notificationId)
            .dbNotificationId(100L)
            .build();
        TaskInstance<SendEmailTaskData> taskInstance = new TaskInstance<>(
            VerifyEmailTaskComponent.verifyEmailTask.getTaskName(),
            "task-1",
            taskData
        );

        Notification notification = mock(Notification.class);
        when(notification.getStatus()).thenReturn("delivered");
        when(notificationClient.getNotificationById(notificationId)).thenReturn(notification);

        CompletionHandler<SendEmailTaskData> handler = task.execute(taskInstance, executionContext);

        assertThat(handler).isInstanceOf(CompletionHandler.OnCompleteRemove.class);
        verify(notificationService).updateNotificationStatus(100L, "delivered");
    }

    @Test
    @DisplayName("Should update status to permanent-failure when notification status is not delivered")
    void executeStatusIsNotDeliveredUpdateStatusToPermanentFailureAndRemove() throws Exception {
        String notificationId = "notif-uuid-123";
        SendEmailTaskData taskData = SendEmailTaskData.builder()
            .taskId("task-1")
            .notificationId(notificationId)
            .dbNotificationId(100L)
            .build();
        TaskInstance<SendEmailTaskData> taskInstance = new TaskInstance<>(
            VerifyEmailTaskComponent.verifyEmailTask.getTaskName(),
            "task-1",
            taskData
        );

        Notification notification = mock(Notification.class);
        when(notification.getStatus()).thenReturn("technical-failure");
        when(notificationClient.getNotificationById(notificationId)).thenReturn(notification);

        CompletionHandler<SendEmailTaskData> handler = task.execute(taskInstance, executionContext);

        assertThat(handler).isInstanceOf(CompletionHandler.OnCompleteRemove.class);
        verify(notificationService).updateNotificationStatus(100L, NotificationStatus.PERMANENT_FAILURE.toString());
    }

    @Test
    @DisplayName("Should throw NotificationException when NotificationClientException occurs")
    void executeClientExceptionThrowNotificationException() throws Exception {
        String notificationId = "notif-uuid-123";
        SendEmailTaskData taskData = SendEmailTaskData.builder()
            .taskId("task-1")
            .notificationId(notificationId)
            .dbNotificationId(100L)
            .build();
        TaskInstance<SendEmailTaskData> taskInstance = new TaskInstance<>(
            VerifyEmailTaskComponent.verifyEmailTask.getTaskName(),
            "task-1",
            taskData
        );

        NotificationClientException clientException = mock(NotificationClientException.class);
        when(clientException.getMessage()).thenReturn("API error");
        when(notificationClient.getNotificationById(notificationId)).thenThrow(clientException);

        assertThatThrownBy(() -> task.execute(taskInstance, executionContext))
            .isInstanceOf(NotificationException.class)
            .hasMessage("Failed to fetch notification, please try again.")
            .hasCause(clientException);
    }
}
