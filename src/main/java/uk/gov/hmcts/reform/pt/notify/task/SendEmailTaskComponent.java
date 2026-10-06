package uk.gov.hmcts.reform.pt.notify.task;

import com.github.kagkarlsson.scheduler.task.CompletionHandler;
import com.github.kagkarlsson.scheduler.task.FailureHandler;
import com.github.kagkarlsson.scheduler.task.SchedulableInstance;
import com.github.kagkarlsson.scheduler.task.TaskDescriptor;
import com.github.kagkarlsson.scheduler.task.TaskInstance;
import com.github.kagkarlsson.scheduler.task.helper.CustomTask;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
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
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static uk.gov.hmcts.reform.pt.notify.task.VerifyEmailTaskComponent.verifyEmailTask;

@Slf4j
@Component
public class SendEmailTaskComponent {
    private static final String SEND_EMAIL_TASK_NAME = "send-email-task";

    public static final TaskDescriptor<SendEmailTaskData> sendEmailTask =
        TaskDescriptor.of(SEND_EMAIL_TASK_NAME, SendEmailTaskData.class);

    private final NotificationService notificationService;
    private final NotificationClient notificationClient;
    private final CaseNotificationRepository caseNotificationRepository;
    private final int maxRetriesSendEmail;
    private final Duration sendingBackoffDelay;
    private final Duration statusCheckTaskDelay;

    public SendEmailTaskComponent(
        NotificationService notificationService,
        NotificationClient notificationClient,
        CaseNotificationRepository caseNotificationRepository,
        @Value("${notify.send-email.max-retries}") int maxRetriesSendEmail,
        @Value("${notify.send-email.backoff-delay-seconds}") Duration sendingBackoffDelay,
        @Value("${notify.check-status.task-delay-seconds}") Duration statusCheckTaskDelay
    ) {
        this.notificationService = notificationService;
        this.notificationClient = notificationClient;
        this.caseNotificationRepository = caseNotificationRepository;
        this.maxRetriesSendEmail = maxRetriesSendEmail;
        this.sendingBackoffDelay = sendingBackoffDelay;
        this.statusCheckTaskDelay = statusCheckTaskDelay;
    }

    @Bean
    public CustomTask<SendEmailTaskData> sendEmailTask() {
        return Tasks.custom(sendEmailTask)
            .onFailure(new FailureHandler.MaxRetriesFailureHandler<>(
                maxRetriesSendEmail,
                new FailureHandler.ExponentialBackoffFailureHandler<>(sendingBackoffDelay)
            ))
            .execute((taskInstance, executionContext) -> {
                SendEmailTaskData taskData = taskInstance.getData();
                log.info("Processing send email task: {} with DB notification ID: {}",
                         taskData.getTaskId(), taskData.getDbNotificationId());

                Optional<CaseNotificationEntity> notificationOpt =
                    caseNotificationRepository.findById(taskData.getDbNotificationId());
                if (notificationOpt.isEmpty()) {
                    log.error("Notification not found with ID: {}", taskData.getDbNotificationId());
                    return new CompletionHandler.OnCompleteRemove<>();
                }


                try {
                    final String templateId = taskData.getTemplateId();
                    final String destinationAddress = taskData.getEmailAddress();
                    final Map<String, Object> personalisation = taskData.getPersonalisation();
                    final String referenceId = UUID.randomUUID().toString();

                    SendEmailResponse response = notificationClient.sendEmail(
                        templateId,
                        destinationAddress,
                        personalisation,
                        referenceId
                    );

                    if (response.getNotificationId() == null) {
                        log.error("Email service returned null notification ID for task: {}", taskData.getTaskId());
                        throw new NotificationException(
                            "Null notification ID from email service",
                            new IllegalStateException("Email service returned null notification ID")
                        );
                    }

                    notificationService.updateNotificationAfterSending(
                        taskData.getDbNotificationId(),
                        response.getNotificationId()
                    );

                    String notificationId = response.getNotificationId().toString();
                    log.info("Request sent successfully. Notification ID: {}", notificationId);

                    SendEmailTaskData nextState = taskData.toBuilder()
                        .notificationId(notificationId)
                        .build();

                    return new CompletionHandler.OnCompleteReplace<>(
                        currentInstance -> SchedulableInstance.of(
                            new TaskInstance<>(
                                verifyEmailTask
                                    .getTaskName(),
                                currentInstance.getId(), nextState),
                            Instant.now().plus(statusCheckTaskDelay)
                        )
                    );
                } catch (NotificationClientException e) {
                    log.error("NotificationClient error sending email: {}", e.getMessage(), e);

                    if (isPermanentFailure(e)) {
                        CaseNotificationEntity caseNotification = notificationOpt.get();
                        notificationService.updateNotificationStatus(
                            caseNotification.getId(),
                            NotificationStatus.PERMANENT_FAILURE.toString()
                        );
                        return new CompletionHandler.OnCompleteRemove<>();
                    } else {
                        throw new NotificationException("Email temporarily failed to send.", e);
                    }
                }
            });
    }

    private boolean isPermanentFailure(NotificationClientException e) {
        int httpStatusCode = e.getHttpResult();
        return httpStatusCode == 400 || httpStatusCode == 403;
    }
}
