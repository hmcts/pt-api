package uk.gov.hmcts.reform.pt.functional.tests;

import net.serenitybdd.annotations.Steps;
import net.serenitybdd.annotations.Title;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.reform.pt.ccd.CaseType;
import uk.gov.hmcts.reform.pt.functional.config.TestConstants;
import uk.gov.hmcts.reform.pt.functional.steps.ApiSteps;
import uk.gov.hmcts.reform.pt.functional.steps.BaseApi;
import uk.gov.hmcts.reform.pt.functional.testutils.PayloadLoader;
import uk.gov.hmcts.reform.pt.functional.testutils.PtIdamTokenClient;
import uk.gov.hmcts.reform.pt.functional.testutils.RandomNumberUtil;
import uk.gov.hmcts.reform.pt.functional.testutils.TestCaseCleanUp;

import java.util.Map;

@Tag("Functional")
@ExtendWith(SerenityJUnit5Extension.class)
@EnabledIfEnvironmentVariable(named = "CCD_ENABLED", matches = "true")
class CaseIssueToRespondentEventCallbackTests extends BaseApi {

    @Steps
    ApiSteps apiSteps;

    private static final Long caseId = RandomNumberUtil.generateRandomNumber(16);
    private static final Long caseIdWithoutLandlord = RandomNumberUtil.generateRandomNumber(16);
    private static final String caseType = CaseType.getCaseType();

    @Title("caseIssueToRespondent submit event callback - issues a landlord access code and returns 200")
    @Test
    void caseIssueToRespondentSubmitEventCallbackTest() {
        submitEvent(caseId, "citizen-create-application",
                    "/payloads/citizenCreateApplication-submitEventCallbackRequest.json",
                    PtIdamTokenClient.UserType.citizenUser);
        submitEvent(caseId, "citizen-update-application",
                    "/payloads/citizenUpdateApplication-withLandlord-submitEventCallbackRequest.json",
                    PtIdamTokenClient.UserType.citizenUser);
        submitEvent(caseId, "case-issue-to-respondent",
                    "/payloads/caseIssueToRespondent-submitEventCallbackRequest.json",
                    PtIdamTokenClient.UserType.caseworkerUser);
        apiSteps.checkStatusCode(200);
    }

    @Title("caseIssueToRespondent submit event callback - fails when the case has no landlord")
    @Test
    void caseIssueToRespondentFailsWithoutLandlord() {
        submitEvent(caseIdWithoutLandlord, "citizen-create-application",
                    "/payloads/citizenCreateApplication-submitEventCallbackRequest.json",
                    PtIdamTokenClient.UserType.citizenUser);
        apiSteps.checkStatusCode(200);

        submitEvent(caseIdWithoutLandlord, "case-issue-to-respondent",
                    "/payloads/caseIssueToRespondent-submitEventCallbackRequest.json",
                    PtIdamTokenClient.UserType.caseworkerUser);
        apiSteps.checkStatusCode(500);
    }

    private void submitEvent(Long id, String eventId, String payload, PtIdamTokenClient.UserType user) {
        apiSteps.requestIsPreparedWithAppropriateValues();
        apiSteps.theRequestContainsValidServiceToken(TestConstants.PT_API);
        apiSteps.theRequestContainsValidIdamToken(user);
        apiSteps.theRequestContainsIdempotencyKeyHeader();
        apiSteps.theRequestContainsTheQueryParameter("eventId", eventId);
        apiSteps.theRequestContainsBody(PayloadLoader.load(payload, Map.of("caseTypeId", caseType, "caseId", id)));
        apiSteps.callIsSubmittedToTheEndpoint("SubmitEventCallback", "POST");
    }

    @AfterAll
    static void tearDown() {
        TestCaseCleanUp.deleteCase(caseId);
        TestCaseCleanUp.deleteCase(caseIdWithoutLandlord);
    }
}
