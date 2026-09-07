package uk.gov.hmcts.reform.pt.testingsupport.endpoint;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.exception.InvalidAuthTokenException;
import uk.gov.hmcts.reform.pt.idam.IdamAuthenticator;
import uk.gov.hmcts.reform.pt.idam.UpstreamThrottling;
import uk.gov.hmcts.reform.pt.idam.User;
import uk.gov.hmcts.reform.pt.idam.UserInfo;
import uk.gov.hmcts.reform.pt.service.PTCaseService;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestingSupportController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = "testing-support.enabled=true")
class TestingSupportControllerTest {

    private static final long CASE_REFERENCE = 1234567890123456L;
    private static final String AUTH = "Bearer token";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PTCaseService ptCaseService;

    @MockitoBean
    private UpstreamThrottling upstreamThrottling;

    @MockitoBean
    private IdamAuthenticator idamAuthenticator;

    private void givenUserWithRoles(List<String> roles) {
        UserInfo userInfo = UserInfo.builder().uid("uid").roles(roles).build();
        when(idamAuthenticator.validateAuthToken(AUTH)).thenReturn(new User(AUTH, userInfo));
    }

    @Test
    void shouldDeleteCaseAndReturnNoContentForSystemUser() throws Exception {
        givenUserWithRoles(List.of("caseworker", "caseworker-pt", "pt-system-update"));
        doNothing().when(ptCaseService).deleteCase(CASE_REFERENCE);

        mockMvc.perform(delete("/testing-support/cases/{caseReference}", CASE_REFERENCE)
                            .header("Authorization", AUTH))
            .andExpect(status().isNoContent());

        verify(ptCaseService).deleteCase(CASE_REFERENCE);
    }

    @Test
    void shouldReturnForbiddenForCitizenUser() throws Exception {
        givenUserWithRoles(List.of("citizen"));

        mockMvc.perform(delete("/testing-support/cases/{caseReference}", CASE_REFERENCE)
                            .header("Authorization", AUTH))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message").value("Requires the pt-system-update role"));

        verify(ptCaseService, never()).deleteCase(CASE_REFERENCE);
    }

    @Test
    void shouldReturnForbiddenWhenUserHasNoRoles() throws Exception {
        givenUserWithRoles(null);

        mockMvc.perform(delete("/testing-support/cases/{caseReference}", CASE_REFERENCE)
                            .header("Authorization", AUTH))
            .andExpect(status().isForbidden());

        verify(ptCaseService, never()).deleteCase(CASE_REFERENCE);
    }

    @Test
    void shouldReturnUnauthorisedWhenTokenIsInvalid() throws Exception {
        when(idamAuthenticator.validateAuthToken(AUTH))
            .thenThrow(new InvalidAuthTokenException("The Authorization token provided is expired or invalid"));

        mockMvc.perform(delete("/testing-support/cases/{caseReference}", CASE_REFERENCE)
                            .header("Authorization", AUTH))
            .andExpect(status().isUnauthorized());

        verify(ptCaseService, never()).deleteCase(CASE_REFERENCE);
    }

    @Test
    void shouldReturnNotFoundWhenCaseDoesNotExist() throws Exception {
        givenUserWithRoles(List.of("pt-system-update"));
        doThrow(new CaseNotFoundException(CASE_REFERENCE)).when(ptCaseService).deleteCase(CASE_REFERENCE);

        mockMvc.perform(delete("/testing-support/cases/{caseReference}", CASE_REFERENCE)
                            .header("Authorization", AUTH))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("No case found with reference " + CASE_REFERENCE));

        verify(ptCaseService).deleteCase(CASE_REFERENCE);
    }
}
