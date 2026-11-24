package uk.gov.hmcts.reform.pt.exception;

public class DocAssemblyException extends RuntimeException {

    public DocAssemblyException(String message) {
        super(message);
    }

    public DocAssemblyException(String message, Throwable cause) {
        super(message, cause);
    }
}
