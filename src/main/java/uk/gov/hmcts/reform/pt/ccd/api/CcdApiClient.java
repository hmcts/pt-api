package uk.gov.hmcts.reform.pt.ccd.api;

import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseDataContent;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.Event;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.pt.ccd.CaseType;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.event.EventId;
import uk.gov.hmcts.reform.pt.exception.CcdException;
import uk.gov.hmcts.reform.pt.security.IdamTokenProvider;

@Component
@AllArgsConstructor
public class CcdApiClient {
    private final CoreCaseDataApi ccdApi;
    private final IdamTokenProvider idamTokenProvider;
    private final AuthTokenGenerator authTokenGenerator;

    public StartEventResponse startEvent(EventId eventId) {
        String idamToken = idamTokenProvider.getAuthToken();
        String s2sToken = authTokenGenerator.generate();
        StartEventResponse startEventResponse;
        try {
            startEventResponse = ccdApi.startCase(
                idamToken,
                s2sToken,
                CaseType.getCaseType(),
                eventId.name()
            );
        } catch (FeignException e) {
            throw new CcdException(String.format("Failed to start %s event in CCD: %s", eventId.name(), e.getMessage()));
        }
        return startEventResponse;
    }

    public CaseDetails submitCaseCreation(PTCase ptCase, EventId eventId, String eventToken) {
        String idamToken = idamTokenProvider.getAuthToken();
        String s2sToken = authTokenGenerator.generate();
        CaseDataContent caseDateContent = CaseDataContent.builder()
            .data(ptCase)
            .event(Event.builder().id(eventId.name()).build())
            .eventToken(eventToken)
            .build();
        CaseDetails caseDetails;
        try {
            caseDetails = ccdApi.submitCaseCreation(
                idamToken,
                s2sToken,
                CaseType.getCaseType(),
                caseDateContent
            );
        } catch (FeignException e) {
            throw new CcdException(String.format("Failed to submit case creation for event %s: %s", eventToken, e.getMessage()));
        }
        return caseDetails;
    }
}
