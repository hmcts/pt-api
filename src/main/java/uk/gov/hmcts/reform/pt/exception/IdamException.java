package uk.gov.hmcts.reform.pt.exception;

public class IdamException extends RuntimeException {

    public IdamException(String message) {
        super(message);
    }

    public IdamException(String message, Throwable cause) {
        super(message, cause);
    }

}
