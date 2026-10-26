package uk.gov.hmcts.reform.pt.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.filters.ServiceAuthFilter;
import uk.gov.hmcts.reform.authorisation.validators.ServiceAuthTokenValidator;
import uk.gov.hmcts.reform.pt.controllers.ApplicationController;
import uk.gov.hmcts.reform.pt.controllers.RootController;
import uk.gov.hmcts.reform.pt.exception.InvalidAuthTokenException;
import uk.gov.hmcts.reform.pt.idam.IdamAuthenticator;
import uk.gov.hmcts.reform.pt.idam.UpstreamThrottling;
import uk.gov.hmcts.reform.pt.idam.User;
import uk.gov.hmcts.reform.pt.idam.UserInfo;
import uk.gov.hmcts.reform.pt.security.SecurityContextService;
import uk.gov.hmcts.reform.pt.service.CaseApplicationService;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ApplicationController.class, RootController.class})
@Import({SecurityConfiguration.class, SecurityConfigurationIT.ServiceAuthFilterConfig.class})
@ActiveProfiles("integration")
class SecurityConfigurationIT {

    private static final String S2S_TOKEN = "Bearer s2s-token";
    private static final String INVALID_S2S_TOKEN = "Bearer INVALID S2S TOKEN";
    private static final String INVALID_S2S_NAME = "INVALID_S2S_NAME";
    private static final String IDAM_TOKEN = "Bearer idam-token";
    private static final String INVALID_IDAM_TOKEN = "INVALID IDAM TOKEN";
    private static final String AUTHORISED_S2S_SERVICE = "pt_api";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServiceAuthorisationApi serviceAuthorisationApi;

    @MockitoBean
    private IdamAuthenticator idamAuthenticator;

    @MockitoBean
    private SecurityContextService securityContextService;

    /**
     * Need these because @WebMvcTest won't find them and they are used in the ApplicationController's ctor
     */
    @MockitoBean
    private CaseApplicationService caseApplicationService;

    @MockitoBean
    private UpstreamThrottling upstreamThrottling;

    /**
     * ServiceAuthFilter normally comes from ServiceAuthAutoConfiguration (from the
     * service-auth-provider-java-client HMCTS library), which @WebMvcTest doesn't load in this test, so we build it
     * here against the mocked ServiceAuthorisationApi.
     */
    @TestConfiguration
    static class ServiceAuthFilterConfig {
        @Bean
        ServiceAuthFilter serviceAuthFilter(ServiceAuthorisationApi serviceAuthorisationApi,
                                            @Value("${idam.s2s-authorised.services}") List<String> authorisedServices) {
            return new ServiceAuthFilter(new ServiceAuthTokenValidator(serviceAuthorisationApi), authorisedServices);
        }

        @Bean
        FilterRegistrationBean<ServiceAuthFilter> disableServiceAuthFilterRegistration(ServiceAuthFilter filter) {
            FilterRegistrationBean<ServiceAuthFilter> registration = new FilterRegistrationBean<>(filter);
            registration.setEnabled(false);
            return registration;
        }
    }

    @BeforeEach
    void setUp() {
        when(serviceAuthorisationApi.getServiceName(S2S_TOKEN)).thenReturn(AUTHORISED_S2S_SERVICE);
        when(serviceAuthorisationApi.getServiceName(INVALID_S2S_TOKEN)).thenReturn(INVALID_S2S_NAME);

        UUID randomUuid = UUID.randomUUID();
        when(securityContextService.getCurrentUserDetails())
            .thenReturn(UserInfo.builder().uid(randomUuid.toString()).build());

        User user = new User(IDAM_TOKEN, UserInfo.builder().uid(randomUuid.toString()).build());

        when(idamAuthenticator.validateAuthToken(any())).thenReturn(user);
        when(idamAuthenticator.validateAuthToken(INVALID_IDAM_TOKEN))
            .thenThrow(new InvalidAuthTokenException("Invalid Idam token"));
    }

    @DisplayName("Idam and Service filter invocations should be invoked")
    @ParameterizedTest(name = "Idam and Service filter should be invoked for {0}")
    @ValueSource(strings = {"/applications", "/applications/1234", "/cases"})
    void shouldInvokeIdamFilter_whenRequestingAKnownPath(String path) throws Exception {
        mockMvc.perform(get(path)
                            .header(ServiceAuthFilter.AUTHORISATION, S2S_TOKEN)
                            .header(HttpHeaders.AUTHORIZATION, IDAM_TOKEN));

        verify(serviceAuthorisationApi).getServiceName(S2S_TOKEN);
        verify(idamAuthenticator).validateAuthToken(IDAM_TOKEN);
    }

    @DisplayName("Idam should reject unknown endpoint")
    @ParameterizedTest(name = "Idam should reject unknown endpoint {0}")
    @ValueSource(strings = {"/fred/xyz", "/fred/1234/xyz"})
    void shouldReturnForbiddenFromIdam_whenRequestingAnUnknownPath(String path) throws Exception {
        mockMvc.perform(get(path)
                            .header(ServiceAuthFilter.AUTHORISATION, S2S_TOKEN)
                            .header(HttpHeaders.AUTHORIZATION, IDAM_TOKEN))
            .andExpect(status().isForbidden());

        verify(serviceAuthorisationApi).getServiceName(S2S_TOKEN);
        verifyNoInteractions(idamAuthenticator);
    }

    @DisplayName("Service and Idam checks for anonymous paths")
    @ParameterizedTest(name = "Service and Idam checks not run for {0}")
    @ValueSource(strings = {"/", "/health", "/health/liveness"})
    void shouldSkipServiceAndIdamFilters_whenDefinedAnonymousPathsRequested(String path) throws Exception {
        mockMvc.perform(get(path)
                            .header(ServiceAuthFilter.AUTHORISATION, S2S_TOKEN)
                            .header(HttpHeaders.AUTHORIZATION, IDAM_TOKEN));

        verifyNoInteractions(serviceAuthorisationApi);
        verifyNoInteractions(idamAuthenticator);
    }

    @Test
    @DisplayName("Should reject request when no service or idam tokens provided")
    void shouldBeUnauthorized_whenNeitherServiceIdentifierOrIdamTokenProvided() throws Exception {
        mockMvc.perform(get("/abc"))
            .andExpect(status().isUnauthorized());

        verifyNoInteractions(serviceAuthorisationApi);
        verifyNoInteractions(idamAuthenticator);
    }

    @Test
    @DisplayName("Service checker should return forbidden for unknown service regardless of path")
    void shouldReturnForbiddenFromServiceAuth_whenServiceTokenInvalid_andRequestingValidPath() throws Exception {
        mockMvc.perform(get("/applications/123")
                            .header(ServiceAuthFilter.AUTHORISATION, INVALID_S2S_TOKEN)
                            .header(HttpHeaders.AUTHORIZATION, IDAM_TOKEN))
            .andExpect(status().isForbidden());

        verifyNoInteractions(idamAuthenticator);
        verify(serviceAuthorisationApi).getServiceName(INVALID_S2S_TOKEN);
    }

    @Test
    @DisplayName("Should reject request when invalid Idam token provided")
    void shouldReturnNotAuthorizedFromIdamFilter_whenRequestingAKnownPathWithInvalidIdamToken() throws Exception {
        mockMvc.perform(get("/applications")
                            .header(ServiceAuthFilter.AUTHORISATION, S2S_TOKEN)
                            .header(HttpHeaders.AUTHORIZATION, INVALID_IDAM_TOKEN))
            .andExpect(status().isUnauthorized());

        verify(serviceAuthorisationApi).getServiceName(S2S_TOKEN);
        verify(idamAuthenticator).validateAuthToken(INVALID_IDAM_TOKEN);
    }
}
