package uk.gov.hmcts.reform.pt.ccd.event.managecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.DecentralisedConfigBuilder;
import uk.gov.hmcts.ccd.sdk.api.EventPayload;
import uk.gov.hmcts.ccd.sdk.api.callback.SubmitResponse;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.ccd.domain.UserRole;

import static uk.gov.hmcts.ccd.sdk.api.Permission.CRU;
import static uk.gov.hmcts.reform.pt.ccd.domain.UserRole.PT_CASE_WORKER;
import static uk.gov.hmcts.reform.pt.ccd.event.EventId.CASE_REQUEST_VETTING_INFORMATION;

@Component
@RequiredArgsConstructor
public class CaseRequestVettingInformation implements CCDConfig<PTCase, State, UserRole> {

    @Override
    public void configureDecentralised(DecentralisedConfigBuilder<PTCase, State, UserRole> configBuilder) {
        configBuilder
            .decentralisedEvent(CASE_REQUEST_VETTING_INFORMATION.getId(), this::submit)
            .forState(State.CASE_ISSUED)
            .name(CASE_REQUEST_VETTING_INFORMATION.getName())
            .description(CASE_REQUEST_VETTING_INFORMATION.getName())
            .grant(CRU, PT_CASE_WORKER)
            .showSummary()
            .endButtonLabel("Submit");
    }

    private SubmitResponse<State> submit(EventPayload<PTCase, State> payload) {
        return SubmitResponse.<State>builder().state(State.CASE_PROGRESSION).build();
    }
}
