package uk.gov.hmcts.reform.pt.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class KeyVaultSecretsCheckPostProcessor implements EnvironmentPostProcessor {

    private static final List<String> EXPECTED_SECRET_ENV_VARS = List.of(
        "PT_DB_PASSWORD",
        "PT_API_S2S_SECRET",
        "IDAM_CLIENT_SECRET",
        "PT_IDAM_SYSTEM_USERNAME",
        "PT_IDAM_SYSTEM_PASSWORD"
    );

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        List<String> missing = EXPECTED_SECRET_ENV_VARS.stream()
            .filter(name -> {
                String value = environment.getProperty(name);
                return value == null || value.isBlank();
            })
            .collect(Collectors.toList());

        if (!missing.isEmpty()) {
            // Use System.err / stdout here — logging framework may not be
            // initialised yet this early in the lifecycle
            log.error("=========================================");
            log.error("KEYVAULT SECRETS CHECK: MISSING SECRETS:");
            missing.forEach(name -> System.err.println("  - " + name));
            log.error("=========================================");
        } else {
            log.info("KeyVault secrets check: all expected secrets present.");
        }
    }
}
