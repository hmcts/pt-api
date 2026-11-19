package uk.gov.hmcts.reform.pt.feesandpay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.hmcts.reform.pt.feesandpay.exception.FeeAlreadyPaidException;
import uk.gov.hmcts.reform.pt.feesandpay.exception.FeePaymentNotFoundException;
import uk.gov.hmcts.reform.pt.feesandpay.model.CardPaymentStatusResponse;
import uk.gov.hmcts.reform.pt.feesandpay.model.CreateCardPaymentRequest;
import uk.gov.hmcts.reform.pt.feesandpay.model.CreateCardPaymentResponse;
import uk.gov.hmcts.reform.pt.feesandpay.service.PaymentService;
import uk.gov.hmcts.reform.pt.idam.IdamAuthenticator;
import uk.gov.hmcts.reform.pt.idam.UpstreamThrottling;
import uk.gov.hmcts.reform.pt.security.SecurityContextService;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    private static final String S2S_TOKEN = "Bearer s2s-token";
    private static final String SERVICE_REQUEST_REFERENCE = "2026-1234567890";
    private static final String CARD_PAYMENT_URL =
        "/payment/service-request/" + SERVICE_REQUEST_REFERENCE + "/card-payment";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IdamAuthenticator idamAuthenticator;

    @MockitoBean
    private SecurityContextService securityContextService;

    @MockitoBean
    private UpstreamThrottling upstreamThrottling;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void shouldCreateCardPaymentRequest() throws Exception {
        when(paymentService.createCardPaymentRequest(eq(SERVICE_REQUEST_REFERENCE),
                                                     any(CreateCardPaymentRequest.class)))
            .thenReturn(CreateCardPaymentResponse.builder()
                            .paymentReference("RC-1234-5678-9012-3456")
                            .status("Initiated")
                            .nextUrl("https://card.payments.service.gov.uk/secure/abc")
                            .build());

        postValidCardPaymentRequest()
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.paymentReference").value("RC-1234-5678-9012-3456"))
            .andExpect(jsonPath("$.status").value("Initiated"))
            .andExpect(jsonPath("$.nextUrl").value("https://card.payments.service.gov.uk/secure/abc"));
    }

    @ParameterizedTest(name = "missing {0}")
    @ValueSource(strings = {"language", "returnUrl"})
    void shouldReturnBadRequestWhenRequiredFieldMissing(String missingField) throws Exception {
        Map<String, Object> body = new HashMap<>(Map.of(
            "language", "English",
            "returnUrl", "https://pt/payment/return"
        ));
        body.remove(missingField);

        mockMvc.perform(post(CARD_PAYMENT_URL)
                            .header("ServiceAuthorization", S2S_TOKEN)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest());

        verify(paymentService, never()).createCardPaymentRequest(anyString(), any());
    }

    @Test
    void shouldGetCardPaymentStatus() throws Exception {
        when(paymentService.getCardPaymentStatus("RC-1234-5678-9012-3456"))
            .thenReturn(CardPaymentStatusResponse.builder().status("Success").build());

        mockMvc.perform(get("/payment/card-payment/RC-1234-5678-9012-3456/status")
                            .header("ServiceAuthorization", S2S_TOKEN))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("Success"));
    }

    @Test
    void shouldReturnNotFoundWhenNoApplicationFee() throws Exception {
        when(paymentService.createCardPaymentRequest(eq(SERVICE_REQUEST_REFERENCE), any()))
            .thenThrow(new FeePaymentNotFoundException("No fee payment entity found for " + SERVICE_REQUEST_REFERENCE));

        postValidCardPaymentRequest().andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnConflictWhenAlreadyPaid() throws Exception {
        when(paymentService.createCardPaymentRequest(eq(SERVICE_REQUEST_REFERENCE), any()))
            .thenThrow(new FeeAlreadyPaidException(SERVICE_REQUEST_REFERENCE));

        postValidCardPaymentRequest().andExpect(status().isConflict());
    }

    private ResultActions postValidCardPaymentRequest() throws Exception {
        return mockMvc.perform(post(CARD_PAYMENT_URL)
                                   .header("ServiceAuthorization", S2S_TOKEN)
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .content("""
                                   {"language": "English", "returnUrl": "https://pt/payment/return"}
                                   """));
    }
}
