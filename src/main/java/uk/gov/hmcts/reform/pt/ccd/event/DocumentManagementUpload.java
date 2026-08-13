package uk.gov.hmcts.reform.pt.ccd.event;

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
import static uk.gov.hmcts.reform.pt.ccd.domain.State.AWAITING_JUDGMENT;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.CASE_ISSUED;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.CASE_PROGRESSION;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.CASE_STAYED;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.CLOSED;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.HEARING_READINESS;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.PREPARE_FOR_HEARING_CONDUCT_HEARING;
import static uk.gov.hmcts.reform.pt.ccd.domain.UserRole.PT_CASE_WORKER;
import static uk.gov.hmcts.reform.pt.ccd.event.EventId.DOCUMENT_MANAGEMENT_UPLOAD;

@Component
@RequiredArgsConstructor
public class DocumentManagementUpload implements CCDConfig<PTCase, State, UserRole> {

    @Override
    public void configureDecentralised(DecentralisedConfigBuilder<PTCase, State, UserRole> configBuilder) {
        configBuilder
            .decentralisedEvent(DOCUMENT_MANAGEMENT_UPLOAD.getId(), this::submit)
            .forStates(
                CASE_ISSUED,
                CASE_PROGRESSION,
                HEARING_READINESS,
                PREPARE_FOR_HEARING_CONDUCT_HEARING,
                AWAITING_JUDGMENT,
                CLOSED,
                CASE_STAYED
            )
            .showSummary()
            .name(DOCUMENT_MANAGEMENT_UPLOAD.getName())
            .grant(CRU, PT_CASE_WORKER); // TODO: use correct roles when further details are released
    }

    private SubmitResponse<State> submit(EventPayload<PTCase, State> eventPayload) {
        // TODO: implement when further details are released
        return SubmitResponse.<State>builder().build();
    }
}
