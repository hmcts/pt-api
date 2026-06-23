package uk.gov.hmcts.reform.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.flyway")
@Data
public class FlywayProperties {
    private String locations;
    private boolean enabled;
}
