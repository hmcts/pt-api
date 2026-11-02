package uk.gov.hmcts.reform.pt.exception;

public class AccessCodeAlreadyUsedException extends RuntimeException {
    public AccessCodeAlreadyUsedException(String message) {
        super(message);
    }
}
