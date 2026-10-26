package uk.gov.hmcts.reform.pt.exception;

public class AccessCodeExpiredException extends RuntimeException {
    public AccessCodeExpiredException(String message) {
        super(message);
    }
}
