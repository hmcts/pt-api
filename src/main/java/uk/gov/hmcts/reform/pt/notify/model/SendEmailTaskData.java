package uk.gov.hmcts.reform.pt.notify.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder(toBuilder = true)
public class SendEmailTaskData {
    private final String taskId;
    private final String emailAddress;
    private final String templateId;
    private final Map<String, Object> personalisation;
    private final String emailReplyToId;
    private final String notificationId; // GOV.UK Notify notification ID (set after sending)
    private final Long dbNotificationId; // Database notification record ID (set before sending)
}
