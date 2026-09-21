package uk.gov.hmcts.reform.pt.ccd.event.citizen;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.DecentralisedConfigBuilder;
import uk.gov.hmcts.ccd.sdk.api.EventPayload;
import uk.gov.hmcts.ccd.sdk.api.callback.SubmitResponse;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.ccd.domain.UserRole;
import uk.gov.hmcts.reform.pt.service.PTCaseService;

import static uk.gov.hmcts.ccd.sdk.api.Permission.CRU;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.AWAITING_SUBMISSION_TO_HMCTS;
import static uk.gov.hmcts.reform.pt.ccd.domain.UserRole.CITIZEN;
import static uk.gov.hmcts.reform.pt.ccd.event.EventId.CITIZEN_DELETE_APPLICATION;

@Component
@RequiredArgsConstructor
public class CitizenDeleteApplication implements CCDConfig<PTCase, State, UserRole> {

    private static final int EXPIRE_IMMEDIATELY = -1;

    private final PTCaseService ptCaseService;

    @Override
    public void configureDecentralised(DecentralisedConfigBuilder<PTCase, State, UserRole> configBuilder) {
        configBuilder
            .decentralisedEvent(CITIZEN_DELETE_APPLICATION.getId(), this::submit)
            .forStateTransition(AWAITING_SUBMISSION_TO_HMCTS, State.PendingDisposal)
            .name(CITIZEN_DELETE_APPLICATION.getName())
            .grant(CRU, CITIZEN)
            .ttlIncrement(EXPIRE_IMMEDIATELY)
            .showSummary()
            .endButtonLabel("Submit");
    }

    private SubmitResponse<State> submit(EventPayload<PTCase, State> eventPayload) {
        ptCaseService.deleteDocumentsForCase(eventPayload.caseReference());
        return SubmitResponse.defaultResponse();
    }
}
