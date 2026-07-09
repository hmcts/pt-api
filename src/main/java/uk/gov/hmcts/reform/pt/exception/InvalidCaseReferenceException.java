package uk.gov.hmcts.reform.pt.exception;

public class InvalidCaseReferenceException extends RuntimeException {
    public InvalidCaseReferenceException(long caseReference) {
        super("Invalid case reference: " + caseReference);
    }
}
