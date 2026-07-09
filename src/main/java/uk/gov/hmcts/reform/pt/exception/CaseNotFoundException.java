package uk.gov.hmcts.reform.pt.exception;

public class CaseNotFoundException extends RuntimeException {
    public CaseNotFoundException(long caseReference) {
        super("No case found with reference " + caseReference);
    }
}
