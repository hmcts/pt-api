package uk.gov.hmcts.reform.pt.service.document;

public enum DocumentValidationErrors {

    REMOVE_FILE_FIRST("removeFileFirst"),
    TOTAL_TOO_LARGE("totalTooLarge");

    private final String value;

    DocumentValidationErrors(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
