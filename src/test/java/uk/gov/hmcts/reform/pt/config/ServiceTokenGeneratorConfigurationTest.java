
package uk.gov.hmcts.reform.pt.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ServiceTokenGeneratorConfigurationTest {

    private final ServiceAuthorisationApi serviceAuthorisationApi =
        mock(ServiceAuthorisationApi.class);

    private final ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withUserConfiguration(ServiceTokenGeneratorConfiguration.class)
            .withBean(ServiceAuthorisationApi.class, () -> serviceAuthorisationApi)
            .withPropertyValues("idam.s2s-auth.totp_secret=test-secret", "idam.s2s-auth.microservice=pt_api");

    @Test
    @DisplayName("Should create service auth token generator")
    void shouldCreateServiceAuthTokenGenerator() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(AuthTokenGenerator.class);
        });
    }

    @Test
    @DisplayName("Should create lazy configuration")
    void shouldCreateLazyConfiguration() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();

            assertThat(context.getBeanFactory().getBeanDefinition("serviceAuthTokenGenerator").isLazyInit()).isTrue();
        });
    }
}
