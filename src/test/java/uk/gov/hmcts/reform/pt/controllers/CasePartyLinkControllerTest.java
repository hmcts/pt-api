package uk.gov.hmcts.reform.pt.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.hmcts.reform.pt.exception.AccessCodeAlreadyUsedException;
import uk.gov.hmcts.reform.pt.exception.AccessCodeExpiredException;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.exception.InvalidAccessCodeException;
import uk.gov.hmcts.reform.pt.idam.IdamAuthenticator;
import uk.gov.hmcts.reform.pt.idam.UpstreamThrottling;
import uk.gov.hmcts.reform.pt.idam.User;
import uk.gov.hmcts.reform.pt.idam.UserInfo;
import uk.gov.hmcts.reform.pt.service.PartyAccessCodeLinkService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CasePartyLinkController.class)
@AutoConfigureMockMvc(addFilters = false)
class CasePartyLinkControllerTest {

    private static final String AUTH = "Bearer token";
    private static final long CASE_REFERENCE = 1234567890123456L;
    private static final String URL = "/cases/" + CASE_REFERENCE + "/validate-access-code";
    private static final UUID USER_ID = UUID.randomUUID();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IdamAuthenticator idamAuthenticator;

    @MockitoBean
    private UpstreamThrottling upstreamThrottling;

    @MockitoBean
    private PartyAccessCodeLinkService linkService;

    @BeforeEach
    void setUp() {
        UserInfo userInfo = UserInfo.builder().uid(USER_ID.toString()).build();
        when(idamAuthenticator.validateAuthToken(AUTH)).thenReturn(new User(AUTH, userInfo));
    }

    @Test
    void shouldLinkUsingUserIdFromToken() throws Exception {
        postCode("ABCDEFGHJKLM").andExpect(status().isOk());

        verify(linkService).linkPartyByAccessCode(CASE_REFERENCE, "ABCDEFGHJKLM", USER_ID);
    }

    @Test
    void shouldReturn400ForMalformedCode() throws Exception {
        postCode("short").andExpect(status().isBadRequest());

        verify(linkService, never()).linkPartyByAccessCode(anyLong(), anyString(), any());
    }

    @Test
    void shouldReturn400ForInvalidCode() throws Exception {
        givenServiceThrows(new InvalidAccessCodeException("Invalid access code"));

        postCode("ABCDEFGHJKLM")
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Invalid access code"));
    }

    @Test
    void shouldReturn410ForExpiredCode() throws Exception {
        givenServiceThrows(new AccessCodeExpiredException("Access code has expired"));

        postCode("ABCDEFGHJKLM").andExpect(status().isGone());
    }

    @Test
    void shouldReturn409WhenCodeAlreadyUsed() throws Exception {
        givenServiceThrows(new AccessCodeAlreadyUsedException("This access code has already been used"));

        postCode("ABCDEFGHJKLM").andExpect(status().isConflict());
    }

    @Test
    void shouldReturn404WhenCaseNotFound() throws Exception {
        givenServiceThrows(new CaseNotFoundException("Case not found"));

        postCode("ABCDEFGHJKLM").andExpect(status().isNotFound());
    }

    private ResultActions postCode(String code) throws Exception {
        return mockMvc.perform(post(URL)
                                   .header("Authorization", AUTH)
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .content("{\"accessCode\":\"" + code + "\"}"));
    }

    private void givenServiceThrows(RuntimeException ex) {
        doThrow(ex).when(linkService).linkPartyByAccessCode(anyLong(), anyString(), any());
    }
}
