package uk.gov.hmcts.reform.pt.notify.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.pt.entity.AddressEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.notify.model.NotificationRequest;
import uk.gov.hmcts.reform.pt.notify.service.NotificationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static uk.gov.hmcts.reform.pt.notify.template.EmailTemplate.LANDLORD_ACCESS_CODE;

@ExtendWith(MockitoExtension.class)
class LandlordAccessCodeNotificationTest {

    private static final String FRONTEND_URL = "https://pt.aat.platform.hmcts.net/";
    private static final String ACCESS_CODE = "ABCDEFGHJKLM";

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private LandlordAccessCodeNotification notification;

    private PTCaseEntity ptCase;
    private CasePartyEntity applicant;
    private CasePartyEntity landlord;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(notification, "frontendUrl", FRONTEND_URL);

        ptCase = PTCaseEntity.builder().id(10L).caseReference(1234567890123456L).build();
        applicant = CasePartyEntity.builder().firstName("Jane").lastName("Tenant").build();
        landlord = CasePartyEntity.builder().emailAddress("landlord@example.com").build();
    }

    @Test
    void shouldScheduleLandlordAccessCodeEmail() {
        applicant.getAddresses().add(AddressEntity.builder()
                                         .addressLine1("1 High Street")
                                         .addressLine2("Flat 2")
                                         .postTown("Birmingham")
                                         .postcode("B1 1AA")
                                         .build());

        notification.send(ptCase, applicant, landlord, ACCESS_CODE);

        NotificationRequest request = capturedRequest();
        assertThat(request.getTemplate()).isEqualTo(LANDLORD_ACCESS_CODE);
        assertThat(request.getEmailAddress()).isEqualTo("landlord@example.com");
        assertThat(request.getPtCase()).isSameAs(ptCase);
        assertThat(request.getPersonalisation())
            .containsEntry("tenantname", "Jane Tenant")
            .containsEntry("propertyAddress", "1 High Street, Flat 2, Birmingham, B1 1AA")
            .containsEntry("citizenDashboardUrl", FRONTEND_URL)
            .containsEntry("caseReference", "1234567890123456")
            .containsEntry("accessCode", ACCESS_CODE)
            .hasSize(5);
    }

    @Test
    void shouldSkipBlankAddressParts() {
        applicant.getAddresses().add(AddressEntity.builder()
                                         .addressLine1("1 High Street")
                                         .addressLine2("  ")
                                         .addressLine3(null)
                                         .postTown("Birmingham")
                                         .county("")
                                         .postcode("B1 1AA")
                                         .build());

        notification.send(ptCase, applicant, landlord, ACCESS_CODE);

        assertThat(capturedRequest().getPersonalisation())
            .containsEntry("propertyAddress", "1 High Street, Birmingham, B1 1AA");
    }

    @Test
    void shouldUseFirstAddressWhenApplicantHasSeveral() {
        applicant.getAddresses().add(AddressEntity.builder().addressLine1("1 High Street").postcode("B1 1AA").build());
        applicant.getAddresses().add(AddressEntity.builder().addressLine1("9 Other Road").postcode("M1 1AA").build());

        notification.send(ptCase, applicant, landlord, ACCESS_CODE);

        assertThat(capturedRequest().getPersonalisation())
            .containsEntry("propertyAddress", "1 High Street, B1 1AA");
    }

    @Test
    void shouldSendEmptyAddressWhenApplicantHasNone() {
        notification.send(ptCase, applicant, landlord, ACCESS_CODE);

        assertThat(capturedRequest().getPersonalisation()).containsEntry("propertyAddress", "");
    }

    @Test
    void shouldOmitMissingNamePartsFromTenantName() {
        applicant.setFirstName(null);

        notification.send(ptCase, applicant, landlord, ACCESS_CODE);

        assertThat(capturedRequest().getPersonalisation()).containsEntry("tenantname", "Tenant");
    }

    private NotificationRequest capturedRequest() {
        ArgumentCaptor<NotificationRequest> captor = ArgumentCaptor.forClass(NotificationRequest.class);
        verify(notificationService).scheduleEmailNotification(captor.capture());
        return captor.getValue();
    }
}
