package uk.gov.hmcts.reform.pt.feesandpay.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCardPaymentResponse {
    private String paymentReference;
    private String status;
    private String nextUrl;
}
