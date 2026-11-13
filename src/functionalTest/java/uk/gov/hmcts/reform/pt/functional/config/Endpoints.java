package uk.gov.hmcts.reform.pt.functional.config;

import lombok.Getter;

@Getter
public enum Endpoints {

    Applications("/applications"),
    RespondentApplications("/respondent/applications"),
    ReturnApplication("/applications/{caseReference}"),
    SubmitEventCallback("/ccd-persistence/cases"),
    DeleteTestCase("/testing-support/cases/{caseReference}"),
    ValidateAccessCode("/cases/{caseReference}/validate-access-code"),
    IssueLandlordAccessCode("/testing-support/cases/{caseReference}/landlord-access-code");

    private final String resource;

    Endpoints(String resource) {
        this.resource = resource;
    }
}
