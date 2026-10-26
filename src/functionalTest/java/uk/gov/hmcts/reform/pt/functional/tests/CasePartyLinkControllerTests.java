package uk.gov.hmcts.reform.pt.functional.tests;

import io.restassured.http.ContentType;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.annotations.Title;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import net.serenitybdd.rest.SerenityRest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.hmcts.reform.pt.functional.config.TestConstants;
import uk.gov.hmcts.reform.pt.functional.steps.ApiSteps;
import uk.gov.hmcts.reform.pt.functional.testutils.CcdCaseCreator;
import uk.gov.hmcts.reform.pt.functional.testutils.PtIdamTokenClient;
import uk.gov.hmcts.reform.pt.functional.testutils.TestCaseCleanUp;

import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.reform.pt.functional.config.AuthTokens.citizenUserIdamToken;
import static uk.gov.hmcts.reform.pt.functional.config.AuthTokens.ptApiS2sToken;
import static uk.gov.hmcts.reform.pt.functional.config.AuthTokens.systemUserIdamToken;

@Tag("Functional")
@ExtendWith(SerenityJUnit5Extension.class)
@EnabledIfEnvironmentVariable(named = "CCD_ENABLED", matches = "true")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CasePartyLinkControllerTests {

    @Steps
    ApiSteps apiSteps;

    private static final String WRONG_CODE = "ABCDEFGHJKLM";
    private static Long caseId;
    private static String landlordToken;
    private static String accessCode;

    @BeforeAll
    static void createCaseWithLandlordCode() {
        // Create and update through the CCD data store so CCD has a case pointer and the right version
        caseId = CcdCaseCreator.createCitizenCase(citizenUserIdamToken, ptApiS2sToken);

        CcdCaseCreator.updateCitizenCase(caseId, "citizen-update-application",
                         Map.of("landlordDetails", Map.of(
                             "representativeType", "noLettingAgentOrRepresentative",
                             "landlordPartyDetails", Map.of(
                                 "firstName", "Larry",
                                 "lastName", "Landlord",
                                 "emailAddress", "simulate-delivered@notifications.service.gov.uk"))),
                         citizenUserIdamToken, ptApiS2sToken);

        ApiSteps steps = new ApiSteps();
        steps.requestIsPreparedWithAppropriateValues();
        steps.theRequestContainsValidServiceToken(TestConstants.PT_API);
        steps.theRequestContainsValidIdamToken(PtIdamTokenClient.UserType.systemUser);
        steps.theRequestContainsThePathParameter("caseReference", caseId.toString());
        steps.callIsSubmittedToTheEndpoint("IssueLandlordAccessCode", "POST");
        steps.checkStatusCode(200);
        accessCode = steps.theResponseBodyValue("accessCode");

        landlordToken = PtIdamTokenClient.generateToken(PtIdamTokenClient.UserType.citizenUser); // a different user
    }

    @Order(1)
    @Title("Validate access code - 400 when the code is malformed")
    @Test
    void malformedCode() {
        postCode("short", TestConstants.PT_FRONTEND);
        apiSteps.checkStatusCode(400);
    }

    @Order(2)
    @Title("Validate access code - 400 when the code is wrong")
    @Test
    void wrongCode() {
        postCode(WRONG_CODE, TestConstants.PT_FRONTEND);
        apiSteps.checkStatusCode(400);
    }

    @Order(3)
    @Title("Validate access code - 200 and the landlord is linked when the code is correct")
    @Test
    void correctCode() {
        postCode(accessCode, TestConstants.PT_FRONTEND);
        apiSteps.checkStatusCode(200);
    }

    @Order(4)
    @Title("Validate access code - 409 when the code has already been used")
    @Test
    void codeAlreadyUsed() {
        postCode(accessCode, TestConstants.PT_FRONTEND);
        apiSteps.checkStatusCode(409);
    }

    @Order(5)
    @Title("Validate access code - 403 when an unauthorised S2S token is used")
    @Test
    void unauthorisedService() {
        apiSteps.requestIsPreparedWithAppropriateValues();
        apiSteps.theRequestContainsUnauthorisedServiceToken();
        apiSteps.theRequestContainsIdamToken(landlordToken);
        apiSteps.theRequestContainsThePathParameter("caseReference", caseId.toString());
        apiSteps.theRequestContainsBody(Map.of("accessCode", WRONG_CODE));
        apiSteps.callIsSubmittedToTheEndpoint("ValidateAccessCode", "POST");
        apiSteps.checkStatusCode(403);
    }

    private void postCode(String code, String service) {
        apiSteps.requestIsPreparedWithAppropriateValues();
        apiSteps.theRequestContainsValidServiceToken(service);
        apiSteps.theRequestContainsIdamToken(landlordToken);
        apiSteps.theRequestContainsThePathParameter("caseReference", caseId.toString());
        apiSteps.theRequestContainsBody(Map.of("accessCode", code));
        apiSteps.callIsSubmittedToTheEndpoint("ValidateAccessCode", "POST");
    }

    @AfterAll
    static void tearDown() {
        String landlordId = SerenityRest.given()
            .baseUri(System.getenv("IDAM_API_URL"))
            .header(TestConstants.AUTHORIZATION, "Bearer " + landlordToken)
            .get("/o/userinfo")
            .then().extract().path("uid");

        SerenityRest.given()
            .baseUri(System.getenv("DATA_STORE_URL_BASE"))
            .header(TestConstants.AUTHORIZATION, "Bearer " + systemUserIdamToken)
            .header(TestConstants.SERVICE_AUTHORIZATION, ptApiS2sToken)
            .contentType(ContentType.JSON)
            .body(Map.of("case_users", List.of(Map.of(
                "case_id", caseId.toString(),
                "user_id", landlordId,
                "case_role", "[DEFENDANT]"))))
            .delete("/case-users");

        TestCaseCleanUp.deleteCase(caseId);
    }
}
