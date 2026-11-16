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
    public ResponseEntity<ErrorResponse> handleCaseNotFoundException(CaseNotFoundException cnfException) {
        // Exception contains message text and case number, so just log that
        log.warn(cnfException.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(cnfException.getMessage()));
    }

    @ExceptionHandler(InvalidCaseReferenceException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCaseReferenceException(
        InvalidCaseReferenceException icrException) {
        // Exception contains message text and case number, so just log that
        log.warn(icrException.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(icrException.getMessage()));
    }
}
