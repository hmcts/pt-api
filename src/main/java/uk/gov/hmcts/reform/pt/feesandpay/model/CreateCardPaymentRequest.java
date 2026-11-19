package uk.gov.hmcts.reform.pt.feesandpay.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCardPaymentRequest {
    @NotNull
    private String language;
    @NotNull
    private String returnUrl;
}
