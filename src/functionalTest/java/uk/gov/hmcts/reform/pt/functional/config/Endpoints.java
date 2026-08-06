package uk.gov.hmcts.reform.pt.functional.config;

public enum Endpoints {

    Applications("/applications"),
    ReturnApplication("/applications/{caseReference}"),
    StartEventCallback("/callbacks/about-to-start"),
    SubmitEventCallback("/ccd-persistence/cases");

    private final String resource;

    Endpoints(String resource) {
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }
}
