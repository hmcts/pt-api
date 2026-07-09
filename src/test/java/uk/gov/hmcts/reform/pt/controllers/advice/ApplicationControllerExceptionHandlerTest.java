package uk.gov.hmcts.reform.pt.controllers.advice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.exception.InvalidCaseReferenceException;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationControllerExceptionHandlerTest {

    private ApplicationControllerExceptionHandler underTest;

    @BeforeEach
    void setUp() {
        underTest = new ApplicationControllerExceptionHandler();
    }

    @Test
    void shouldHandleCaseNotFoundException() {
        long caseReference = 12345L;
        CaseNotFoundException caseNotFoundException = new CaseNotFoundException(caseReference);
        String expectedErrorMessage = "No case found with reference " + caseReference;

        ResponseEntity<ErrorResponse> responseEntity =
            underTest.handleCaseNotFoundException(caseNotFoundException);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().message()).isEqualTo(expectedErrorMessage);
    }

    @Test
    void shouldHandleInvalidCaseReferenceException() {
        long caseReference = 1234567890L;
        String expectedErrorMessage = "Invalid case reference: " + caseReference;
        InvalidCaseReferenceException exception = new InvalidCaseReferenceException(caseReference);

        ResponseEntity<ErrorResponse> response =
            underTest.handleInvalidCaseReferenceException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo(expectedErrorMessage);
    }

}
