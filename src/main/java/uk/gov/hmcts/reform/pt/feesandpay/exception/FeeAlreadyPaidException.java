package uk.gov.hmcts.reform.pt.feesandpay.exception;

public class FeeAlreadyPaidException extends RuntimeException {
    public FeeAlreadyPaidException(String serviceRequestReference) {
        super("Service request " + serviceRequestReference + " has already been paid");
    }
}
