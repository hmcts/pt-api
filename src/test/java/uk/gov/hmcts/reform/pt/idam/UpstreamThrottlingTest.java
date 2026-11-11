package uk.gov.hmcts.reform.pt.idam;

import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UpstreamThrottlingTest {

    private final UpstreamThrottling throttling =
        new UpstreamThrottling("30", Set.of("invalid_token_response", "temporarily_unavailable"));

    @Test
    @DisplayName("Should return configured retry after seconds")
    void shouldReturnConfiguredRetryAfterSeconds() {
        assertThat(throttling.retryAfterSeconds()).isEqualTo("30");
    }

    @Test
    @DisplayName("Should return custom retry after seconds")
    void shouldReturnCustomRetryAfterSeconds() {
        UpstreamThrottling customThrottling = new UpstreamThrottling("60", Set.of());

        assertThat(customThrottling.retryAfterSeconds()).isEqualTo("60");
    }

    @ParameterizedTest
    @ValueSource(ints = {429})
    @DisplayName("Should return true for REST client status")
    void shouldReturnTrueForRestClientStatus(int status) {
        assertThat(throttling.isUpstreamUnavailable(restException(status))).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 503})
    @DisplayName("Should return false for REST client status")
    void shouldReturnFalseForRestClientStatus(int status) {
        assertThat(throttling.isUpstreamUnavailable(restException(status))).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid_token_response", "temporarily_unavailable"})
    @DisplayName("Should return true for configured OAuth2 error")
    void shouldReturnTrueForConfiguredOAuth2Error(String errorCode) {
        assertThat(throttling.isUpstreamUnavailable(oauth2Exception(errorCode))).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid_grant", "invalid_client", "access_denied"})
    @DisplayName("Should return false for unconfigured OAuth2 error")
    void shouldReturnFalseForUnconfiguredOAuth2Error(String errorCode) {
        assertThat(throttling.isUpstreamUnavailable(oauth2Exception(errorCode))).isFalse();
    }

    @ParameterizedTest
    @ValueSource(ints = {429, 500, 503, -1})
    @DisplayName("Should return true for configured Feign status")
    void shouldReturnTrueForFeignStatus(int status) {
        assertThat(throttling.isUpstreamUnavailable(feignException(status))).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 401, 404})
    @DisplayName("Should return false for configured Feign status")
    void shouldReturnFalseForFeignStatus(int status) {
        assertThat(throttling.isUpstreamUnavailable(feignException(status))).isFalse();
    }

    @Test
    @DisplayName("Should return true for nested REST client 429")
    void shouldReturnTrueForNestedRestClient429() {
        RuntimeException exception = new RuntimeException(restException(429));

        assertThat(throttling.isUpstreamUnavailable(exception)).isTrue();
    }

    @Test
    @DisplayName("Should return true for nested Feign failure")
    void shouldReturnTrueForNestedFeignFailure() {
        RuntimeException exception = new RuntimeException(feignException(503));

        assertThat(throttling.isUpstreamUnavailable(exception)).isTrue();
    }

    @Test
    @DisplayName("Should return true for nested OAuth2 throttle error")
    void shouldReturnTrueForNestedOAuth2ThrottleError() {
        RuntimeException exception = new RuntimeException(oauth2Exception("temporarily_unavailable"));

        assertThat(throttling.isUpstreamUnavailable(exception)).isTrue();
    }

    @Test
    @DisplayName("Should return false for ordinary exception")
    void shouldReturnFalseForOrdinaryException() {
        assertThat(throttling.isUpstreamUnavailable(new RuntimeException("Something went wrong"))).isFalse();
    }

    @Test
    @DisplayName("Should return false for null exception")
    void shouldReturnFalseForNullException() {
        assertThat(throttling.isUpstreamUnavailable(null)).isFalse();
    }

    @Test
    @DisplayName("Should return false when throttle exception is beyond maximum depth")
    void shouldReturnFalseWhenThrottleExceptionIsBeyondMaximumDepth() {
        Throwable exception = restException(429);

        for (int i = 0; i < 10; i++) {
            exception = new RuntimeException(exception);
        }

        assertThat(throttling.isUpstreamUnavailable(exception)).isFalse();
    }

    private static HttpClientErrorException restException(int status) {
        return HttpClientErrorException.create(
            HttpStatus.valueOf(status),
            "Test response",
            null,
            null,
            null
        );
    }

    private static OAuth2AuthorizationException oauth2Exception(String errorCode) {
        return new OAuth2AuthorizationException(new OAuth2Error(errorCode));
    }

    private static FeignException feignException(int status) {
        Request request = Request.create(
            Request.HttpMethod.GET,
            "https://idam.example.com",
            Map.of(),
            null,
            StandardCharsets.UTF_8,
            null
        );

        return FeignException.errorStatus(
            "IDAM",
            feign.Response.builder()
                .status(status)
                .reason("Test response")
                .request(request)
                .headers(Map.of())
                .build()
        );
    }
}
