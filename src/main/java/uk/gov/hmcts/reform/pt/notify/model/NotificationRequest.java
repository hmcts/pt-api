package uk.gov.hmcts.reform.pt.notify.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.notify.template.EmailTemplate;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private EmailTemplate template;
    private String emailAddress;
    private Map<String, Object> personalisation;
    private String emailReplyToId;
    private PTCaseEntity ptCase;
}
