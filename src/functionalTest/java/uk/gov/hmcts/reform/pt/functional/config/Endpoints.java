package uk.gov.hmcts.reform.pt.functional.config;

public enum Endpoints {

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
