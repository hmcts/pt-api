package uk.gov.hmcts.reform.pt.ccd.event.feeandpay;

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
import static uk.gov.hmcts.reform.pt.ccd.event.EventId.HWF_INVALID_REFERENCE;

@Component
@RequiredArgsConstructor
public class HwFInvalidReference implements CCDConfig<PTCase, State, UserRole> {

    @Override
    public void configureDecentralised(DecentralisedConfigBuilder<PTCase, State, UserRole> configBuilder) {
        configBuilder
            .decentralisedEvent(HWF_INVALID_REFERENCE.getId(), this::submit)
            .forState(State.PENDING_CASE_ISSUED)
            .name(HWF_INVALID_REFERENCE.getName())
            .description(HWF_INVALID_REFERENCE.getName())
            .grant(CRU, PT_CASE_WORKER)
            .showSummary()
            .endButtonLabel("Submit");
    }

    private SubmitResponse<State> submit(EventPayload<PTCase, State> payload) {
        return SubmitResponse.defaultResponse();
    }
}
