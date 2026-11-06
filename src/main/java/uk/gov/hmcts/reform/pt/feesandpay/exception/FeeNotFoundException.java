package uk.gov.hmcts.reform.pt.feesandpay.exception;

public class FeeNotFoundException extends RuntimeException {
    public FeeNotFoundException(String message) {
        super(message);
    }

    public FeeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
