package uk.gov.hmcts.reform.pt.feesandpay.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCardPaymentRequest {
    @NotNull
    private String language;
    @NotNull
    private String returnUrl;
}
