package uk.gov.hmcts.reform.pt.notify.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationStatus {
    CREATED("created"),
    SENDING("sending"),
    DELIVERED("delivered"),
    PERMANENT_FAILURE("permanent-failure"),
    TEMPORARY_FAILURE("temporary-failure"),
    TECHNICAL_FAILURE("technical-failure"),
    SCHEDULED("scheduled"),
    PENDING_SCHEDULE("pending-schedule"),
    SUBMITTED("submitted");

    private final String value;

    @Override
    public String toString() {
        return value;
    }

    public static NotificationStatus fromString(String status) {
        for (NotificationStatus notificationStatus : NotificationStatus.values()) {
            if (notificationStatus.value.equalsIgnoreCase(status)) {
                return notificationStatus;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + status);
    }
}
