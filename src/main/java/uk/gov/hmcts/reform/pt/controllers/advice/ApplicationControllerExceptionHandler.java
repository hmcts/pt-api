package uk.gov.hmcts.reform.pt.controllers.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.gov.hmcts.reform.pt.controllers.ApplicationController;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.exception.InvalidCaseReferenceException;

@Slf4j
@RestControllerAdvice(assignableTypes = ApplicationController.class)
public class ApplicationControllerExceptionHandler {
    @ExceptionHandler(CaseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCaseNotFoundException(CaseNotFoundException caseNotFoundException) {
        log.error("Case not found", caseNotFoundException);
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(caseNotFoundException.getMessage()));
    }

    @ExceptionHandler(InvalidCaseReferenceException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCaseReferenceException(InvalidCaseReferenceException ex) {
        log.error("Invalid case reference", ex);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(ex.getMessage()));
    }
}
