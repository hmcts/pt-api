package uk.gov.hmcts.reform.pt.notify.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.pt.notify.template.EmailTemplate;
import uk.gov.service.notify.NotificationClient;

import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "notify")
public class GovNotifyConfiguration {

    private String apiUrl;
    private String apiKey;
    private Map<String, String> templates;

    @Bean
    public NotificationClient notificationClient() {
        return new NotificationClient(apiKey, apiUrl);
    }

    public String getTemplateId(EmailTemplate template) {
        if (templates == null || templates.isEmpty()) {
            throw new IllegalStateException("Notification templates are not configured");
        }

        String templateId = templates.get(template.getTemplateKey());

        if (templateId == null) {
            throw new IllegalArgumentException(
                "Missing template for key: " + template.getTemplateKey()
            );
        }

        return templateId;
    }
}
