package uk.gov.hmcts.reform.pt.functional.config;

public enum Endpoints {

    Applications("/applications"),
    ReturnApplication("/applications/{caseReference}"),
    SubmitEventCallback("/ccd-persistence/cases"),
    DeleteTestCase("/testing-support/cases/{caseReference}");

    private final String resource;

    Endpoints(String resource) {
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }
}
