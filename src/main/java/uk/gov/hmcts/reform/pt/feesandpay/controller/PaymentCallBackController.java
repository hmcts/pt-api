package uk.gov.hmcts.reform.pt.feesandpay.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.pt.feesandpay.model.PaymentStatusCallback;
import uk.gov.hmcts.reform.pt.feesandpay.service.PaymentService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@AllArgsConstructor
public class PaymentCallBackController {

    private final PaymentService paymentService;

    @PutMapping(path = "/payment-update", consumes = APPLICATION_JSON_VALUE)
    @Operation(
        summary = "This endpoint is invoked by the CCPay service based on the service request callback",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    name = "Paid service request callback",
                    value = """
                    {
                      "service_request_reference": "2025-1712345678901",
                      "ccd_case_number": "1712345678901234",
                      "service_request_amount": 47.00,
                      "service_request_status": "Paid",
                      "payment": {
                        "payment_amount": 47.00,
                        "payment_reference": "RC-1712-3456-7890-1234",
                        "payment_method": "card",
                        "case_reference": "1712345678901234"
                      }
                    }
                    """
                )
            )
        )
    )
    @ApiResponse(
        responseCode = "204",
        description = "Payment callback processed successfully. No response body is returned."
    )
    public ResponseEntity<Void> processPaymentCallback(
        @RequestHeader(value = "ServiceAuthorization") String s2sToken,
        @RequestBody PaymentStatusCallback paymentStatusCallback) {
        paymentService.processPaymentResponse(paymentStatusCallback);
        return ResponseEntity.noContent().build();
    }
}
