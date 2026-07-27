package uk.gov.hmcts.reform.pt.controllers.advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.hmcts.reform.pt.exception.IdamException;
import uk.gov.hmcts.reform.pt.exception.InvalidAuthTokenException;
import uk.gov.hmcts.reform.pt.idam.UpstreamThrottling;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final UpstreamThrottling upstreamThrottling;

    @ExceptionHandler(InvalidAuthTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAuth(InvalidAuthTokenException ex) {
        log.error("Invalid authentication token", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(IdamException.class)
    public ResponseEntity<ErrorResponse> handleIdamException(IdamException ex) {
        log.error("IDAM call failed", ex);
        if (upstreamThrottling.isUpstreamUnavailable(ex)) {
            return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .header(HttpHeaders.RETRY_AFTER, upstreamThrottling.retryAfterSeconds())
                .body(new ErrorResponse("Authentication service temporarily unavailable, please retry"));
        }
        // Generic message to avoid leaking upstream OAuth2 error descriptions / internal URLs.
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("Authentication service error"));
    }
}
