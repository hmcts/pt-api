package uk.gov.hmcts.reform.pt.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;

import static org.assertj.core.api.Assertions.assertThat;

class FlywayConfigurationTest {

    private final ApplicationContextRunner contextRunner =
        new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(FlywayConfiguration.class));

    @Test
    @DisplayName("Should register NoOp migration strategy when properties are missing")
    void shouldRegisterNoOpMigrationStrategyWhenPropertiesAreMissing() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(FlywayMigrationStrategy.class);
            assertThat(context.getBean(FlywayMigrationStrategy.class))
                .isInstanceOf(FlywayNoOpStrategy.class);
        });
    }

    @Test
    @DisplayName("Should register NoOp migration strategy when enabled")
    void shouldRegisterNoOpMigrationStrategyWhenEnabled() {
        contextRunner
            .withPropertyValues("spring.flyway.enabled=true", "flyway.noop.strategy=true")
            .run(context -> {
                assertThat(context).hasSingleBean(FlywayMigrationStrategy.class);
                assertThat(context.getBean(FlywayMigrationStrategy.class))
                    .isInstanceOf(FlywayNoOpStrategy.class);
            });
    }

    @Test
    @DisplayName("Should not register migration strategy when NoOp strategy is disabled")
    void shouldNotRegisterMigrationStrategyWhenNoOpStrategyIsDisabled() {
        contextRunner
            .withPropertyValues("flyway.noop.strategy=false")
            .run(context -> assertThat(context).doesNotHaveBean(FlywayMigrationStrategy.class));
    }

    @Test
    @DisplayName("Should not load configuration when Flyway is disabled")
    void shouldNotLoadConfigurationWhenFlywayIsDisabled() {
        contextRunner
            .withPropertyValues("spring.flyway.enabled=false")
            .run(context -> assertThat(context).doesNotHaveBean(FlywayMigrationStrategy.class));
    }
}
