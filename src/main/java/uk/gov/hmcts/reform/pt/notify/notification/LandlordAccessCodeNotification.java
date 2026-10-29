package uk.gov.hmcts.reform.pt.notify.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.CasePropertyAddressEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.notify.model.NotificationRequest;
import uk.gov.hmcts.reform.pt.notify.service.NotificationService;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.hmcts.reform.pt.notify.template.EmailTemplate.LANDLORD_ACCESS_CODE;

@Component
@RequiredArgsConstructor
public class LandlordAccessCodeNotification {

    private final NotificationService notificationService;

    @Value("${pt-frontend.url}")
    private String frontendUrl;

    public void send(PTCaseEntity ptCase, CasePartyEntity applicant, CasePartyEntity landlord, String accessCode) {
        Map<String, Object> personalisation = new HashMap<>();
        personalisation.put("tenantname", fullName(applicant));
        personalisation.put("propertyAddress", formatAddress(ptCase.getPropertyAddress()));
        personalisation.put("citizenDashboardUrl", frontendUrl);
        personalisation.put("caseReference", ptCase.getCaseReference().toString());
        personalisation.put("accessCode", accessCode);

        notificationService.scheduleEmailNotification(NotificationRequest.builder()
            .template(LANDLORD_ACCESS_CODE)
            .emailAddress(landlord.getEmailAddress())
            .personalisation(personalisation)
            .ptCase(ptCase)
            .build());
    }

    private String fullName(CasePartyEntity party) {
        return Stream.of(party.getFirstName(), party.getLastName())
            .filter(StringUtils::hasText)
            .collect(Collectors.joining(" "));
    }

    private String formatAddress(CasePropertyAddressEntity address) {
        if (address == null) {
            return "";
        }
        return Stream.of(
                address.getAddressLine1(),
                address.getAddressLine2(),
                address.getTownCity(),
                address.getCounty(),
                address.getPostcode())
            .filter(StringUtils::hasText)
            .collect(Collectors.joining(", "));
    }
}
