package uk.gov.hmcts.reform.pt.exception;

public class InvalidAuthTokenException extends RuntimeException {

    public InvalidAuthTokenException(String message, Exception cause) {
        super(message, cause);
    }

    public InvalidAuthTokenException(String message) {
        super(message);
    }
}
