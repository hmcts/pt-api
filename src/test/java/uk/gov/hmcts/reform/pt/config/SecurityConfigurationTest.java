package uk.gov.hmcts.reform.pt.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import uk.gov.hmcts.reform.authorisation.filters.ServiceAuthFilter;
import uk.gov.hmcts.reform.pt.idam.IdamAuthenticationFilter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SecurityConfigurationTest {

    private final ServiceAuthFilter serviceAuthFilter = mock(ServiceAuthFilter.class);

    private final IdamAuthenticationFilter idamAuthFilter = mock(IdamAuthenticationFilter.class);

    private final WebApplicationContextRunner contextRunner =
        new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration.class))
            .withBean(ServiceAuthFilter.class, () -> serviceAuthFilter)
            .withBean(IdamAuthenticationFilter.class, () -> idamAuthFilter)
            .withUserConfiguration(SecurityConfiguration.class);

    @Test
    @DisplayName("Should load security configuration")
    void shouldLoadSecurityConfiguration() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(SecurityConfiguration.class);
        });
    }

    @Test
    @DisplayName("Should create web security customizer")
    void shouldCreateWebSecurityCustomizer() {
        contextRunner.run(context -> assertThat(context).hasSingleBean(WebSecurityCustomizer.class));
    }

    @Test
    @DisplayName("Should create security filter chain")
    void shouldCreateSecurityFilterChain() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(SecurityFilterChain.class);
        });
    }

    @Test
    @DisplayName("Should register authentication filters in security filter chain")
    void shouldRegisterAuthenticationFiltersInSecurityFilterChain() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();

            SecurityFilterChain filterChain = context.getBean(SecurityFilterChain.class);

            assertThat(filterChain.getFilters()).contains(serviceAuthFilter, idamAuthFilter);
        });
    }

    @Test
    @DisplayName("Should disable default authentication filters")
    void shouldDisableDefaultAuthenticationFilters() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();

            SecurityFilterChain filterChain =
                context.getBean(SecurityFilterChain.class);

            assertThat(filterChain.getFilters())
                .noneMatch(filter -> filter instanceof BasicAuthenticationFilter
                    || filter instanceof UsernamePasswordAuthenticationFilter
                    || filter instanceof LogoutFilter
                    || filter instanceof CsrfFilter);
        });
    }
}
