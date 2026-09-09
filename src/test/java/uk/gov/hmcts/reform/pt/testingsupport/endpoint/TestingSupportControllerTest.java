package uk.gov.hmcts.reform.pt.testingsupport.endpoint;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.idam.IdamAuthenticationFilter;
import uk.gov.hmcts.reform.pt.idam.UpstreamThrottling;
import uk.gov.hmcts.reform.pt.service.PTCaseService;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = TestingSupportController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, classes = IdamAuthenticationFilter.class))
@TestPropertySource(properties = "testing-support.enabled=true")
class TestingSupportControllerTest {

    private static final long CASE_REFERENCE = 1234567890123456L;
    private static final String SYSTEM_USER_ROLE = "pt-system-update";
    private static final String CITIZEN_ROLE = "citizen";

    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityTestConfig {

        @Bean
        @SuppressWarnings("PMD.SignatureDeclareThrowsException")
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PTCaseService ptCaseService;

    @MockitoBean
    private UpstreamThrottling upstreamThrottling;

    @Test
    @WithMockUser(authorities = SYSTEM_USER_ROLE)
    void shouldDeleteCaseAndReturnNoContentForSystemUser() throws Exception {
        doNothing().when(ptCaseService).deleteCase(CASE_REFERENCE);

        mockMvc.perform(delete("/testing-support/cases/{caseReference}", CASE_REFERENCE))
            .andExpect(status().isNoContent());

        verify(ptCaseService).deleteCase(CASE_REFERENCE);
    }

    @Test
    @WithMockUser(authorities = SYSTEM_USER_ROLE)
    void shouldReturnNotFoundWhenCaseDoesNotExist() throws Exception {
        doThrow(new CaseNotFoundException(CASE_REFERENCE)).when(ptCaseService).deleteCase(CASE_REFERENCE);

        mockMvc.perform(delete("/testing-support/cases/{caseReference}", CASE_REFERENCE))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("No case found with reference " + CASE_REFERENCE));

        verify(ptCaseService).deleteCase(CASE_REFERENCE);
    }

    @Test
    @WithMockUser(authorities = CITIZEN_ROLE)
    void shouldReturnForbiddenForNonSystemUser() throws Exception {
        mockMvc.perform(delete("/testing-support/cases/{caseReference}", CASE_REFERENCE))
            .andExpect(status().isForbidden());

        verify(ptCaseService, never()).deleteCase(anyLong());
    }

    @Test
    void shouldReturnForbiddenWhenUnauthenticated() throws Exception {
        mockMvc.perform(delete("/testing-support/cases/{caseReference}", CASE_REFERENCE))
            .andExpect(status().isForbidden());

        verify(ptCaseService, never()).deleteCase(anyLong());
    }
}
