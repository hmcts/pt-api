package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.pt.ccd.api.CcdApiClient;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.event.EventId;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.model.CreateApplicationRequest;
import uk.gov.hmcts.reform.pt.model.CreateApplicationResponse;
import uk.gov.hmcts.reform.pt.repository.PTCaseRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PTCaseServiceTest {

    @Mock
    private PTCaseRepository ptCaseRepository;

    @Mock
    private CcdApiClient ccdApi;

    @Captor
    private ArgumentCaptor<PTCaseEntity> ptCaseEntityCaptor;

    @InjectMocks
    private PTCaseService ptCaseService;

    @Test
    @DisplayName("Should save a case entity built from the case reference, user id and PTCase")
    void createCaseFromPTCase() {
        long caseReference = 1234567890123456L;
        UUID userId = UUID.randomUUID();
        PTCase ptCase = PTCase.builder()
            .firstName("John")
            .lastName("Smith")
            .email("john.smith@example.com")
            .postcode("SW1A 1AA")
            .applicationType("myself")
            .build();

        ptCaseService.createCase(caseReference, userId, ptCase);

        verify(ptCaseRepository).save(ptCaseEntityCaptor.capture());
        PTCaseEntity savedEntity = ptCaseEntityCaptor.getValue();

        assertThat(savedEntity.getCaseReference()).isEqualTo(caseReference);
        assertThat(savedEntity.getUserId()).isEqualTo(userId);
        assertThat(savedEntity.getFirstName()).isEqualTo("John");
        assertThat(savedEntity.getLastName()).isEqualTo("Smith");
        assertThat(savedEntity.getEmail()).isEqualTo("john.smith@example.com");
        assertThat(savedEntity.getPostcode()).isEqualTo("SW1A 1AA");
        assertThat(savedEntity.getApplicationType()).isEqualTo("myself");
    }

    @Test
    @DisplayName("Should start and submit a CCD event, persist the case and return the case reference")
    void createCaseFromRequest() {
        UUID userId = UUID.randomUUID();
        CreateApplicationRequest request = CreateApplicationRequest.builder()
            .firstName("Jane")
            .lastName("Doe")
            .email("jane.doe@example.com")
            .postcode("SW1A 1AA")
            .applicationType("myself")
            .build();

        when(ccdApi.startEvent(EventId.createNewApplication)).thenReturn(getTestStartEventResponse());
        when(ccdApi.submitCaseCreation(any(PTCase.class), eq(EventId.createNewApplication), eq("token"))).thenReturn(getTestCaseDetails());

        CreateApplicationResponse response = ptCaseService.createCase(request, userId);

        assertThat((Long) response.getCaseReference()).isEqualTo(1234567890123456L);

        verify(ptCaseRepository).save(ptCaseEntityCaptor.capture());
        PTCaseEntity savedEntity = ptCaseEntityCaptor.getValue();
        assertThat(savedEntity.getCaseReference()).isEqualTo(1234567890123456L);
        assertThat(savedEntity.getUserId()).isEqualTo(userId);
        assertThat(savedEntity.getFirstName()).isEqualTo("Jane");
        assertThat(savedEntity.getLastName()).isEqualTo("Doe");
        assertThat(savedEntity.getEmail()).isEqualTo("jane.doe@example.com");
        assertThat(savedEntity.getPostcode()).isEqualTo("SW1A 1AA");
        assertThat(savedEntity.getApplicationType()).isEqualTo("myself");
    }

    private StartEventResponse getTestStartEventResponse() {
        return StartEventResponse.builder()
            .eventId("foo")
            .caseDetails(null)
            .token("token")
            .build();
    }

    private CaseDetails getTestCaseDetails() {
        return CaseDetails.builder()
            .id(1234567890123456L)
            .build();
    }
}
