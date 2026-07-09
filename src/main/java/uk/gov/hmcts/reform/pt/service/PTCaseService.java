package uk.gov.hmcts.reform.pt.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
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

@Service
@AllArgsConstructor
public class PTCaseService {
    private final PTCaseRepository ptCaseRepository;
    private final CcdApiClient ccdApi;

    public void createCase(
        long caseReference,
        UUID userId,
        PTCase ptCase
    ) {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .caseReference(caseReference)
            .userId(userId)
            .firstName(ptCase.getFirstName())
            .lastName(ptCase.getLastName())
            .email(ptCase.getEmail())
            .postcode(ptCase.getPostcode())
            .applicationType(ptCase.getApplicationType())
            .build();
        ptCaseRepository.save(ptCaseEntity);
    }

    public CreateApplicationResponse createCase(CreateApplicationRequest request, UUID userId) {
        PTCase ptCase = PTCase.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .postcode(request.getPostcode())
            .applicationType(request.getApplicationType())
            .build();
        StartEventResponse startEventResponse = ccdApi.startEvent(EventId.createNewApplication);
        CaseDetails caseDetails = ccdApi.submitCaseCreation(
            ptCase,
            EventId.createNewApplication,
            startEventResponse.getToken()
        );

        Long caseReference = caseDetails.getId();
        createCase(caseReference, userId, ptCase);

        return CreateApplicationResponse.builder()
            .caseReference(caseReference)
            .build();
    }
}
