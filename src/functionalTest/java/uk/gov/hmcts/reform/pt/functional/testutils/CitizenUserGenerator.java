package uk.gov.hmcts.reform.pt.functional.testutils;

import net.serenitybdd.rest.SerenityRest;
import uk.gov.hmcts.reform.pt.functional.config.TestConstants;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.reform.pt.functional.testutils.EnvUtils.getEnv;

public class CitizenUserGenerator {

    private static final String BASE_URL = getEnv("IDAM_TESTING_SUPPORT_URL");
    private static final String GENERIC_PASSWORD = TestConstants.GENERIC_PASSWORD;
    private static final String AUTHORIZATION
        = "Bearer " + PtIdamTokenClient.generateToken(PtIdamTokenClient.UserType.systemUser);

    public static String createCitizenUser() {

        String email = "pt-citizen-" + System.currentTimeMillis() + "@test.com";


        Map<String, Object> user = Map.of(
            "email", email,
            "forename", "PT",
            "surname", "Citizen",
            "roleNames", new String[]{"citizen"}
        );

        Map<String, Object> requestBody = Map.of(
            "password", GENERIC_PASSWORD,
            "user", user
        );

        var response = SerenityRest
            .given()
            .baseUri(BASE_URL)
            .header("Authorization", AUTHORIZATION)
            .contentType(APPLICATION_JSON_VALUE)
            .body(requestBody)
            .post("/test/idam/users");

        if (response.getStatusCode() != 201) {
            throw new RuntimeException(String.format(
                "Failed to generate Citizen User '%s'. Status code: %d.%nResponse: %s",
                email,
                response.getStatusCode(),
                response.prettyPrint()
            ));
        }

        return email;
    }
}
