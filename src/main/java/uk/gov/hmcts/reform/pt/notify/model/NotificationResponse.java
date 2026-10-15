package uk.gov.hmcts.reform.pt.notify.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResponse {
    private String taskId;
    private String status;
    private Long notificationId;
}
