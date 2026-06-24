package uk.gov.hmcts.reform.pt.ccd.event;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.CaseDetails;
import uk.gov.hmcts.ccd.sdk.api.ConfigBuilder;
import uk.gov.hmcts.ccd.sdk.api.Permission;
import uk.gov.hmcts.ccd.sdk.api.callback.AboutToStartOrSubmitResponse;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.ccd.domain.UserRole;

@Profile("dev") // Non-prod event
@Component
public class CreateTestCase implements CCDConfig<PTCase, State, UserRole> {
    @Override
    public void configure(ConfigBuilder<PTCase, State, UserRole> configBuilder) {
        configBuilder
            .event("createTestApplication")
            .initialState(State.AWAITING_SUBMISSION_TO_HMCTS)
            .name("Create test case")
            .aboutToStartCallback(this::start)
            .aboutToSubmitCallback(this::aboutToSubmit)
            .grant(Permission.CRUD, UserRole.CASE_WORKER)
            .fields()
            .page("Create test case")
            .mandatory(PTCase::getApplicantForename)
            .done();
    }

    private AboutToStartOrSubmitResponse<PTCase, State> start(CaseDetails<PTCase, State> caseDetails) {
        PTCase data = caseDetails.getData();
        data.setApplicantForename("Preset value");

        return AboutToStartOrSubmitResponse.<PTCase, State>builder()
            .data(caseDetails.getData())
            .build();
    }

    public AboutToStartOrSubmitResponse<PTCase, State> aboutToSubmit(CaseDetails<PTCase, State> details,
                                                                      CaseDetails<PTCase, State> beforeDetails) {
        // TODO: Whatever you need.
        return AboutToStartOrSubmitResponse.<PTCase, State>builder()
            .data(details.getData())
            .build();
    }
}
