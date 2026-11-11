package uk.gov.hmcts.reform.pt.feesandpay.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.pt.feesandpay.model.FeeType;

import static org.assertj.core.api.Assertions.assertThat;

class FeesConfigurationBindingTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withInitializer(new ConfigDataApplicationContextInitializer())
        .withUserConfiguration(TestConfig.class);

    @Test
    void shouldBindALookupEntryForEveryFeeType() {
        contextRunner.run(context -> {
            FeesConfiguration feesConfiguration = context.getBean(FeesConfiguration.class);

            for (FeeType feeType : FeeType.values()) {
                assertThat(feesConfiguration.getLookup(feeType))
                    .as("fees.lookup entry for %s", feeType)
                    .isNotNull();
            }
        });
    }

    @Configuration
    @EnableConfigurationProperties(FeesConfiguration.class)
    static class TestConfig {
    }
}
