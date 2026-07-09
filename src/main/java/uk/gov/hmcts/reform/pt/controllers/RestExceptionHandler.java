package uk.gov.hmcts.reform.pt.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.exception.IdamException;
import uk.gov.hmcts.reform.pt.exception.InvalidAuthTokenException;
import uk.gov.hmcts.reform.pt.exception.InvalidCaseReferenceException;
import uk.gov.hmcts.reform.pt.idam.UpstreamThrottling;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private final UpstreamThrottling upstreamThrottling;

    @ExceptionHandler(CaseNotFoundException.class)
    public ResponseEntity<Error> handleCaseNotFoundException(CaseNotFoundException caseNotFoundException) {
        log.error("Case not found", caseNotFoundException);
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new Error(caseNotFoundException.getMessage()));
    }

    @ExceptionHandler(InvalidCaseReferenceException.class)
    public ResponseEntity<Error> handleInvalidCaseReferenceException(InvalidCaseReferenceException ex) {
        log.error("Invalid case reference", ex);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new Error(ex.getMessage()));
    }

    @ExceptionHandler(InvalidAuthTokenException.class)
    public ResponseEntity<Error> handleInvalidAuth(InvalidAuthTokenException ex) {
        log.error("Invalid authentication token", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Error(ex.getMessage()));
    }

    @ExceptionHandler(IdamException.class)
    public ResponseEntity<Error> handleIdamException(IdamException ex) {
        log.error("IDAM call failed", ex);
        if (upstreamThrottling.isUpstreamUnavailable(ex)) {
            return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .header(HttpHeaders.RETRY_AFTER, upstreamThrottling.retryAfterSeconds())
                .body(new Error("Authentication service temporarily unavailable, please retry"));
        }
        // Generic message to avoid leaking upstream OAuth2 error descriptions / internal URLs.
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new Error("Authentication service error"));
    }

    public record Error(String message) {}
}
