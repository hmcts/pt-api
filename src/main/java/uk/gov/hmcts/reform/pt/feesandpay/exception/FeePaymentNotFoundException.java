package uk.gov.hmcts.reform.pt.feesandpay.exception;

public class FeePaymentNotFoundException extends RuntimeException {
    public FeePaymentNotFoundException(String message) {
        super(message);
    }
}
