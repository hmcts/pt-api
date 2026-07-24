package uk.gov.hmcts.reform.pt.controllers.advice;

import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.hmcts.reform.pt.exception.IdamException;
import uk.gov.hmcts.reform.pt.exception.InvalidAuthTokenException;
import uk.gov.hmcts.reform.pt.idam.UpstreamThrottling;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler underTest;

    @BeforeEach
    void setUp() {
        UpstreamThrottling upstreamThrottling = new UpstreamThrottling(
            "30",
            Set.of("invalid_token_response", "temporarily_unavailable")
        );

        underTest = new GlobalExceptionHandler(upstreamThrottling);
    }

    @Test
    void shouldHandleInvalidAuthTokenException() {
        String expectedErrorMessage = "Invalid authentication token";
        InvalidAuthTokenException exception = new InvalidAuthTokenException(expectedErrorMessage);

        ResponseEntity<ErrorResponse> responseEntity = underTest.handleInvalidAuth(exception);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().message()).isEqualTo(expectedErrorMessage);
    }

    @Test
    void shouldHandleInvalidAuthTokenExceptionWithCause() {
        String expectedErrorMessage = "Invalid authentication token";
        Exception cause = new RuntimeException("Root cause");
        InvalidAuthTokenException exception = new InvalidAuthTokenException(expectedErrorMessage, cause);

        ResponseEntity<ErrorResponse> responseEntity = underTest.handleInvalidAuth(exception);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().message()).isEqualTo(expectedErrorMessage);
    }

    @Test
    void shouldMapIdamExceptionWrappingOAuth2WithRestClient429ToServiceUnavailable() {
        // Real production shape: Spring's OAuth2 password client wraps a RestClient 429 in
        // OAuth2AuthorizationException, then SystemUpdateUserTokenProvider wraps that in IdamException.
        HttpClientErrorException tooMany = HttpClientErrorException.create(
            HttpStatus.TOO_MANY_REQUESTS, "Too Many Requests", HttpHeaders.EMPTY, new byte[0], null);
        OAuth2Error oauthError = new OAuth2Error(
            "invalid_token_response", "throttled by IDAM", null);
        OAuth2AuthorizationException oauthEx = new OAuth2AuthorizationException(oauthError, tooMany);
        IdamException ex = new IdamException("Unable to get access token response", oauthEx);

        ResponseEntity<ErrorResponse> response = underTest.handleIdamException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getHeaders().getFirst(HttpHeaders.RETRY_AFTER)).isEqualTo("30");
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message())
            .isEqualTo("Authentication service temporarily unavailable, please retry");
    }

    @Test
    void shouldMapIdamExceptionWithDirectRestClient429CauseToServiceUnavailable() {
        // Defense-in-depth: handler should also recognise a RestClient 429 set directly as the cause,
        // not only when buried under OAuth2AuthorizationException.
        HttpClientErrorException tooMany = HttpClientErrorException.create(
            HttpStatus.TOO_MANY_REQUESTS, "Too Many Requests", HttpHeaders.EMPTY, new byte[0], null);
        IdamException ex = new IdamException("Unable to get access token response", tooMany);

        ResponseEntity<ErrorResponse> response = underTest.handleIdamException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getHeaders().getFirst(HttpHeaders.RETRY_AFTER)).isEqualTo("30");
    }

    // Parameterised over the default idam.throttle.oauth2-error-codes set. When the default in
    // application.yaml / UpstreamThrottling changes, mirror it here so coverage stays complete.
    @ParameterizedTest
    @ValueSource(strings = {"invalid_token_response", "temporarily_unavailable"})
    void shouldMapIdamExceptionWithOAuth2ThrottleCodeToServiceUnavailable(String errorCode) {
        OAuth2Error oauthError = new OAuth2Error(errorCode, "throttled by IDAM", null);
        OAuth2AuthorizationException oauthEx = new OAuth2AuthorizationException(oauthError);
        IdamException ex = new IdamException("Unable to get access token response", oauthEx);

        ResponseEntity<ErrorResponse> response = underTest.handleIdamException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getHeaders().getFirst(HttpHeaders.RETRY_AFTER)).isEqualTo("30");
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message())
            .isEqualTo("Authentication service temporarily unavailable, please retry");
    }

    // Non-throttle OAuth2 error codes must NOT return 503 — these are not transient.
    @ParameterizedTest
    @ValueSource(strings = {"server_error", "access_denied", "invalid_grant", "invalid_client", "unauthorized_client"})
    void shouldMapIdamExceptionWithNonThrottleOAuth2CodeToInternalServerError(String errorCode) {
        OAuth2Error oauthError = new OAuth2Error(errorCode, "non-throttle OAuth2 error", null);
        OAuth2AuthorizationException oauthEx = new OAuth2AuthorizationException(oauthError);
        IdamException ex = new IdamException("Unable to get access token response", oauthEx);

        ResponseEntity<ErrorResponse> response = underTest.handleIdamException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getHeaders().getFirst(HttpHeaders.RETRY_AFTER)).isNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Authentication service error");
    }

    @Test
    void shouldMapIdamExceptionWithNon429OAuth2CauseToInternalServerError() {
        HttpClientErrorException internal = HttpClientErrorException.create(
            HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", HttpHeaders.EMPTY, new byte[0], null);
        OAuth2Error oauthError = new OAuth2Error("server_error", "IDAM down", null);
        OAuth2AuthorizationException oauthEx = new OAuth2AuthorizationException(oauthError, internal);
        IdamException ex = new IdamException("Unable to get access token response", oauthEx);

        ResponseEntity<ErrorResponse> response = underTest.handleIdamException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getHeaders().getFirst(HttpHeaders.RETRY_AFTER)).isNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Authentication service error");
    }

    // Feign statuses that count as "upstream unavailable" — connect/read failure (status < 0),
    // throttle (429), or any 5xx. All should surface as 503 + Retry-After.
    @ParameterizedTest
    @ValueSource(ints = {-1, -2, 429, 500, 502, 503, 504, 599})
    void shouldMapIdamExceptionWithFeignUpstreamFailureToServiceUnavailable(int status) {
        FeignException feignEx = mock(FeignException.class);
        when(feignEx.status()).thenReturn(status);
        IdamException ex = new IdamException("Unable to validate authorization token", feignEx);

        ResponseEntity<ErrorResponse> response = underTest.handleIdamException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getHeaders().getFirst(HttpHeaders.RETRY_AFTER)).isEqualTo("30");
    }

    // Feign 4xx (non-throttle) is a real client error, not an availability problem — must NOT
    // map to 503. Covers the boundaries on either side of 500: 499 should be 500-mapped, 500
    // is in the upstream-unavailable test above.
    @ParameterizedTest
    @ValueSource(ints = {400, 401, 403, 404, 422, 499})
    void shouldMapIdamExceptionWithFeignClientErrorToInternalServerError(int status) {
        FeignException feignEx = mock(FeignException.class);
        when(feignEx.status()).thenReturn(status);
        IdamException ex = new IdamException("Unable to validate authorization token", feignEx);

        ResponseEntity<ErrorResponse> response = underTest.handleIdamException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getHeaders().getFirst(HttpHeaders.RETRY_AFTER)).isNull();
    }

    @Test
    void shouldMapIdamExceptionWithNoCauseToInternalServerError() {
        IdamException ex = new IdamException("Unable to get access token response");

        ResponseEntity<ErrorResponse> response = underTest.handleIdamException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Authentication service error");
    }

    @Test
    void shouldUseConfiguredRetryAfterValueInThrottleResponse() {
        // The Retry-After value is read from idam.throttle.retry-after-seconds, not hardcoded.
        GlobalExceptionHandler handler = new GlobalExceptionHandler(
            new UpstreamThrottling("90", Set.of("invalid_token_response")));
        OAuth2Error oauthError = new OAuth2Error("invalid_token_response", "throttled", null);
        IdamException ex = new IdamException(
            "Unable to get access token response", new OAuth2AuthorizationException(oauthError));

        ResponseEntity<ErrorResponse> response = handler.handleIdamException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getHeaders().getFirst(HttpHeaders.RETRY_AFTER)).isEqualTo("90");
    }

    @Test
    void shouldHonourConfiguredOAuth2ThrottleErrorCodes() {
        // A code that is NOT in the configured set must not be treated as throttling.
        GlobalExceptionHandler handler = new GlobalExceptionHandler(
            new UpstreamThrottling("30", Set.of("temporarily_unavailable")));
        OAuth2Error oauthError = new OAuth2Error(
            "invalid_token_response", "not a configured throttle code", null);
        IdamException ex = new IdamException(
            "Unable to get access token response", new OAuth2AuthorizationException(oauthError));

        ResponseEntity<ErrorResponse> response = handler.handleIdamException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void errorRecordShouldHaveMessage() {
        String message = "Test error message";

        ErrorResponse error = new ErrorResponse(message);

        assertThat(error.message()).isEqualTo(message);
    }
}
