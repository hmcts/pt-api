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
class CitizenCreateApplicationEventCallbackTests extends BaseApi {

    @Steps
    ApiSteps apiSteps;

    private static final Long caseId = RandomNumberUtil.generateRandomNumber(16);
    private static final String caseType = CaseType.getCaseType();

    @Title("citizenCreateApplication submit event callback test - creates a case and returns 200")
    @Test
    void citizenCreateApplicationSubmitEventCallbackTest() {
        String requestBody = PayloadLoader.load(
            "/payloads/citizenCreateApplication-submitEventCallbackRequest.json",
            Map.of("caseTypeId", caseType, "caseId", caseId)
        );

        apiSteps.requestIsPreparedWithAppropriateValues();
        apiSteps.theRequestContainsValidServiceToken(TestConstants.PT_API);
        apiSteps.theRequestContainsValidIdamToken(PtIdamTokenClient.UserType.citizenUser);
        apiSteps.theRequestContainsIdempotencyKeyHeader();
        apiSteps.theRequestContainsTheQueryParameter("eventId", "citizen-create-application");
        apiSteps.theRequestContainsBody(requestBody);
        apiSteps.callIsSubmittedToTheEndpoint("SubmitEventCallback", "POST");
        apiSteps.checkStatusCode(200);
        apiSteps.theResponseBodyMatchesTheExpectedResponse(
            "/responses/citizenCreateApplication-submitEventCallbackResponse.json");

        apiSteps.requestIsPreparedWithAppropriateValues();
        apiSteps.theRequestContainsValidServiceToken(TestConstants.PT_API);
        apiSteps.theRequestContainsValidIdamToken(PtIdamTokenClient.UserType.citizenUser);
        apiSteps.theRequestContainsThePathParameter("caseReference", String.valueOf(caseId));
        apiSteps.callIsSubmittedToTheEndpoint("ReturnApplication", "GET");
        apiSteps.checkStatusCode(200);
        apiSteps.theResponseBodyMatchesTheExpectedResponse(
            PayloadLoader.load(
                "/responses/citizenCreateApplication-getApplicationResponse.json",
                Map.of("caseId", caseId)
            ));
    }

    @AfterAll
    static void tearDown() {
        TestCaseCleanUp.deleteCase(caseId);
    }
}
