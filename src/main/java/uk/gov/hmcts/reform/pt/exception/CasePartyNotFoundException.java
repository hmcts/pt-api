package uk.gov.hmcts.reform.pt.exception;

public class CasePartyNotFoundException extends RuntimeException {
    public CasePartyNotFoundException(String message) {
        super(message);
    }
}
