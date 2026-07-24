package uk.gov.hmcts.reform.pt.exception;

public class CcdException extends RuntimeException {
    public CcdException(String message) {
        super(message);
    }

    public CcdException(String message, Throwable cause) {
        super(message, cause);
    }
}
