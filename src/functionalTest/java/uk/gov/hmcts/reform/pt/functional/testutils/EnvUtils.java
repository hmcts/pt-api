package uk.gov.hmcts.reform.pt.functional.testutils;

public class EnvUtils {

    public static String getEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required environment variable: " + name);
        }
        return value;
    }
}
