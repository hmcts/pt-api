package uk.gov.hmcts.reform.pt.functional.steps;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import uk.gov.hmcts.reform.pt.functional.config.TestConstants;
import uk.gov.hmcts.reform.pt.functional.testutils.PtIdamTokenClient;
import uk.gov.hmcts.reform.pt.functional.testutils.ServiceAuthenticationGenerator;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.annotations.Step;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;

import static uk.gov.hmcts.reform.pt.functional.testutils.PtIdamTokenClient.UserType.citizenUser;
import static uk.gov.hmcts.reform.pt.functional.testutils.PtIdamTokenClient.UserType.systemUser;
import static uk.gov.hmcts.reform.pt.functional.testutils.PtIdamTokenClient.UserType.caseworkerUser;

public class ApiSteps {

    private RequestSpecification request;
    private Response response;
    private static final String baseUrl = System.getenv("TEST_URL");
    public static String ptApiS2sToken;
    private static String ptFrontendS2sToken;
    private static String unauthorisedS2sToken;
    public static String systemUserIdamToken;
    public static String citizenUserIdamToken;
    public static String caseworkerUserIdamToken;

    @Step("Generate S2S tokens")
    public static void setUp() {
        ServiceAuthenticationGenerator serviceAuthenticationGenerator = new ServiceAuthenticationGenerator();
        ptApiS2sToken = serviceAuthenticationGenerator.generate();
        ptFrontendS2sToken = serviceAuthenticationGenerator.generate(TestConstants.PT_FRONTEND);
        unauthorisedS2sToken = serviceAuthenticationGenerator.generate(TestConstants.CIVIL_SERVICE);

        systemUserIdamToken = PtIdamTokenClient.generateToken(systemUser);
        citizenUserIdamToken = PtIdamTokenClient.generateToken(citizenUser);
        caseworkerUserIdamToken = PtIdamTokenClient.generateToken(caseworkerUser);

        SerenityRest.given().baseUri(baseUrl);
    }

    @Step("the request contains a valid service token for {0}")
    public void theRequestContainsValidServiceToken(String microservice) {
        final Map<String, String> serviceTokens = Map.of(
            TestConstants.PT_API, ptApiS2sToken,
            TestConstants.PT_FRONTEND, ptFrontendS2sToken
        );

        if (!serviceTokens.containsKey(microservice.toLowerCase())) {
            throw new IllegalArgumentException("Unknown microservice: " + microservice);
        }

        String validS2sToken = serviceTokens.get(microservice.toLowerCase());
        request = request.header(TestConstants.SERVICE_AUTHORIZATION, validS2sToken);
    }

    @Step("the request contains an unauthorised service token")
    public void theRequestContainsUnauthorisedServiceToken() {
        request = request.header(TestConstants.SERVICE_AUTHORIZATION, unauthorisedS2sToken);
    }

    @Step("the request contains an expired service token")
    public void theRequestContainsExpiredServiceToken() {
        String expiredS2sToken = TestConstants.EXPIRED_S2S_TOKEN;
        request = request.header(TestConstants.SERVICE_AUTHORIZATION, expiredS2sToken);
    }

    @Step("Check Health")
    public void getHealth() {
        SerenityRest.given()
            .baseUri(baseUrl)
            .when()
            .get("/health")
            .then()
            .statusCode(200)
            .body("status", equalTo("UP"));
    }
}
