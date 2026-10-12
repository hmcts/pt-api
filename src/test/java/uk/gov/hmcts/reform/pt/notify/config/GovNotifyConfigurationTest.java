package uk.gov.hmcts.reform.pt.notify.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.pt.notify.template.EmailTemplate;
import uk.gov.service.notify.NotificationClient;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GovNotifyConfigurationTest {

    private GovNotifyConfiguration configuration;

    @BeforeEach
    void setUp() {
        configuration = new GovNotifyConfiguration();
        configuration.setApiKey(
            "test-api-key-00000000-0000-0000-0000-000000000000-00000000-0000-0000-0000-000000000000"
        );
        configuration.setApiUrl("https://api.notifications.service.gov.uk");
    }

    @Test
    void shouldCreateNotificationClient() {
        NotificationClient client = configuration.notificationClient();
        assertThat(client).isNotNull();
        assertThat(client.getBaseUrl()).isEqualTo(configuration.getApiUrl());
    }

    @Test
    void shouldReturnTemplateIdIfConfigured() {
        configuration.setTemplates(Map.of("test-template", "template-id-123"));

        String templateId = configuration.getTemplateId(EmailTemplate.TEST_TEMPLATE);

        assertThat(templateId).isEqualTo("template-id-123");
    }

    @Test
    void getTemplateIdIllegalStateExceptionWhenTemplatesNull() {
        configuration.setTemplates(null);

        assertThatThrownBy(() -> configuration.getTemplateId(EmailTemplate.TEST_TEMPLATE))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Notification templates are not configured");
    }

    @Test
    void getTemplateIdIllegalStateExceptionWhenTemplatesEmpty() {
        configuration.setTemplates(Collections.emptyMap());

        assertThatThrownBy(() -> configuration.getTemplateId(EmailTemplate.TEST_TEMPLATE))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Notification templates are not configured");
    }

    @Test
    void getTemplateIdIllegalArgumentExceptionKeyNotFound() {
        configuration.setTemplates(Map.of("other-template", "template-id-456"));

        assertThatThrownBy(() -> configuration.getTemplateId(EmailTemplate.TEST_TEMPLATE))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Missing template for key: test-template");
    }
}
