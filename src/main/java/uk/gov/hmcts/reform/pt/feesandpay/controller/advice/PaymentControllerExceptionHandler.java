package uk.gov.hmcts.reform.pt.feesandpay.controller.advice;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uk.gov.hmcts.reform.pt.controllers.advice.ErrorResponse;
import uk.gov.hmcts.reform.pt.feesandpay.controller.PaymentController;
import uk.gov.hmcts.reform.pt.feesandpay.exception.FeeAlreadyPaidException;
import uk.gov.hmcts.reform.pt.feesandpay.exception.FeePaymentNotFoundException;

@Slf4j
@RestControllerAdvice(assignableTypes = PaymentController.class)
public class PaymentControllerExceptionHandler {

    @ExceptionHandler(FeePaymentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFeePaymentNotFound(FeePaymentNotFoundException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ErrorResponse> handlePaymentNotFound(FeignException.NotFound ex) {
        log.warn("Payment not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Payment not found"));
    }

    @ExceptionHandler(FeeAlreadyPaidException.class)
    public ResponseEntity<ErrorResponse> handleFeeAlreadyPaid(FeeAlreadyPaidException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }
}
