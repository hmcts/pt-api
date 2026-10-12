package uk.gov.hmcts.reform.pt.notify.exception;

import java.io.Serial;

public class NotificationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 6504833464289587150L;

    public NotificationException(String message, Exception cause) {
        super(message, cause);
    }

}
