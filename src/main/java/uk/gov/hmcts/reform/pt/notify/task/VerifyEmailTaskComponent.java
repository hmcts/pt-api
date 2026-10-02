package uk.gov.hmcts.reform.pt.notify.task;

import com.github.kagkarlsson.scheduler.task.CompletionHandler;
import com.github.kagkarlsson.scheduler.task.FailureHandler;
import com.github.kagkarlsson.scheduler.task.TaskDescriptor;
import com.github.kagkarlsson.scheduler.task.helper.CustomTask;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.pt.notify.exception.NotificationException;
import uk.gov.hmcts.reform.pt.notify.model.NotificationStatus;
import uk.gov.hmcts.reform.pt.notify.model.SendEmailTaskData;
import uk.gov.hmcts.reform.pt.notify.service.NotificationService;
import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.time.Duration;
import java.util.Objects;

@Slf4j
@Component
public class VerifyEmailTaskComponent {
    private static final String VERIFY_EMAIL_TASK_NAME = "verify-email-task";

    public static final TaskDescriptor<SendEmailTaskData> verifyEmailTask =
        TaskDescriptor.of(VERIFY_EMAIL_TASK_NAME, SendEmailTaskData.class);

    private final NotificationService notificationService;
    private final NotificationClient notificationClient;
    private final int maxRetriesCheckEmail;
    private final Duration statusCheckBackoffDelay;

    @Autowired
    public VerifyEmailTaskComponent(
        NotificationService notificationService,
        NotificationClient notificationClient,
        @Value("${notify.check-status.max-retries}") int maxRetriesCheckEmail,
        @Value("${notify.check-status.backoff-delay-seconds}") Duration statusCheckBackoffDelay
    ) {
        this.notificationService = notificationService;
        this.notificationClient = notificationClient;
        this.maxRetriesCheckEmail = maxRetriesCheckEmail;
        this.statusCheckBackoffDelay = statusCheckBackoffDelay;
    }

    @Bean
    public CustomTask<SendEmailTaskData> verifyEmailTask() {
        return Tasks.custom(verifyEmailTask)
            .onFailure(new FailureHandler.MaxRetriesFailureHandler<>(
                maxRetriesCheckEmail,
                new FailureHandler.ExponentialBackoffFailureHandler<>(statusCheckBackoffDelay)
            ))
            .execute((taskInstance, executionContext) -> {
                SendEmailTaskData taskData = taskInstance.getData();
                log.info("Verifying email delivery for ID: {}", taskData.getNotificationId());

                try {
                    Notification notification = notificationClient.getNotificationById(taskData.getNotificationId());

                    boolean isDelivered = Objects.equals(
                        notification.getStatus().toLowerCase(),
                        NotificationStatus.DELIVERED.toString()
                    );
                    if (isDelivered) {
                        notificationService.updateNotificationStatus(
                            taskData.getDbNotificationId(),
                            notification.getStatus()
                        );
                    } else {
                        notificationService.updateNotificationStatus(
                            taskData.getDbNotificationId(),
                            NotificationStatus.PERMANENT_FAILURE.toString()
                        );
                    }

                    String status = notification.getStatus();
                    if (NotificationStatus.DELIVERED.toString().equalsIgnoreCase(status)) {
                        log.info("Email successfully delivered: {}", taskData.getTaskId());
                    } else {
                        log.error("Failure with status: {} for task: {}", status, taskData.getTaskId());
                    }
                    return new CompletionHandler.OnCompleteRemove<>();
                } catch (NotificationClientException e) {
                    log.error("Failed to verify status due to API error", e);

                    throw new NotificationException("Failed to fetch notification, please try again.", e);
                }
            });
    }
}
