package uk.gov.hmcts.reform.pt.controllers.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uk.gov.hmcts.reform.pt.controllers.CasePartyLinkController;
import uk.gov.hmcts.reform.pt.exception.AccessCodeAlreadyUsedException;
import uk.gov.hmcts.reform.pt.exception.AccessCodeExpiredException;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.exception.InvalidAccessCodeException;

@Slf4j
@RestControllerAdvice(assignableTypes = CasePartyLinkController.class)
public class CasePartyLinkControllerExceptionHandler {

    @ExceptionHandler(InvalidAccessCodeException.class)
    public ResponseEntity<ErrorResponse> handleInvalid(InvalidAccessCodeException ex) {
        log.warn("Invalid access code: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(AccessCodeExpiredException.class)
    public ResponseEntity<ErrorResponse> handleExpired(AccessCodeExpiredException ex) {
        log.warn("Expired access code: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.GONE).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(AccessCodeAlreadyUsedException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyUsed(AccessCodeAlreadyUsedException ex) {
        log.warn("Access code already used: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(CaseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCaseNotFound(CaseNotFoundException ex) {
        log.warn("Case not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }
}
