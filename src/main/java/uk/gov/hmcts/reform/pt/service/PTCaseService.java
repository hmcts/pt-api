package uk.gov.hmcts.reform.pt.service;

import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.pt.ccd.CaseType;
import uk.gov.hmcts.reform.pt.ccd.api.CcdApiClient;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.event.EventId;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.exception.CcdException;
import uk.gov.hmcts.reform.pt.model.CreateApplicationRequest;
import uk.gov.hmcts.reform.pt.model.CreateApplicationResponse;
import uk.gov.hmcts.reform.pt.repository.PTCaseRepository;
import uk.gov.hmcts.reform.pt.security.IdamTokenProvider;

@Service
@AllArgsConstructor
public class PTCaseService {
    private final PTCaseRepository ptCaseRepository;
    private final CcdApiClient ccdApi;
    private final IdamTokenProvider systemUpdateUserTokenProvider;
    private final AuthTokenGenerator authTokenGenerator;

    public void createCase(
        long caseReference,
        PTCase ptCase
    ) {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .caseReference(caseReference)
            .firstName(ptCase.getFirstName())
            .build();
        ptCaseRepository.save(ptCaseEntity);
    }

    public CreateApplicationResponse createCase(CreateApplicationRequest request) {
        PTCase ptCase = PTCase.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .postcode(request.getPostcode())
            .applicationType(request.getApplicationType())
            .build();
        StartEventResponse startEventResponse = ccdApi.startEvent(EventId.createNewApplication);
        CaseDetails caseDetails = ccdApi.submitCaseCreation(ptCase, EventId.createNewApplication, startEventResponse.getToken());
        long caseReference = createCaseInCcd(ptCase);

        createCase(caseReference, ptCase);

        return CreateApplicationResponse.builder()
            .caseReference(caseReference)
            .build();
    }

    private long createCaseInCcd(PTCase ptCase) {
        StartEventResponse startEventResponse = ccdApi.startEvent()
        String idamToken = systemUpdateUserTokenProvider.getAuthToken();
        String s2sToken = authTokenGenerator.generate();
        String eventId = EventId.createNewApplication.name();
        CaseDetails caseDetails;
        try {
            StartEventResponse startEventResponse = ccdApi.startCase(
                idamToken,
                s2sToken,
                CaseType.getCaseType(),
                eventId
            );
            CaseDataContent caseDateContent = CaseDataContent.builder()
                .data(ptCase)
                .event(Event.builder().id(eventId).build())
                .eventToken(startEventResponse.getToken())
                .build();
            caseDetails = ccdApi.submitCaseCreation(
                idamToken,
                s2sToken,
                CaseType.getCaseType(),
                caseDateContent
            );
        } catch (FeignException e) {
            throw new CcdException(e.getMessage());
        }
        return caseDetails.getId();
    }
}
