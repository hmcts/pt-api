package uk.gov.hmcts.reform.pt.ccd.event.respondent;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.DecentralisedConfigBuilder;
import uk.gov.hmcts.ccd.sdk.api.EventPayload;
import uk.gov.hmcts.ccd.sdk.api.callback.SubmitResponse;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;

import static uk.gov.hmcts.ccd.sdk.api.Permission.CRU;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.DEFENDANT;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.CASE_PROGRESSION;
import static uk.gov.hmcts.reform.pt.ccd.event.EventId.RESPONDENT_UPLOAD_DOCUMENT;

@Component
@RequiredArgsConstructor
public class RespondentUploadDocument implements CCDConfig<PTCase, State, UserRole> {

    @Override
    public void configureDecentralised(DecentralisedConfigBuilder<PTCase, State, UserRole> configBuilder) {
        configBuilder
            .decentralisedEvent(RESPONDENT_UPLOAD_DOCUMENT.getId(), this::submit)
            .forState(CASE_PROGRESSION)
            .name(RESPONDENT_UPLOAD_DOCUMENT.getName())
            .grant(CRU, DEFENDANT);
    }

    private SubmitResponse<State> submit(EventPayload<PTCase, State> eventPayload) {
        return SubmitResponse.<State>builder().build();
    }
}
