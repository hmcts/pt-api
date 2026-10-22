package uk.gov.hmcts.reform.pt.ccd.event.managecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.sdk.api.CCDConfig;
import uk.gov.hmcts.ccd.sdk.api.DecentralisedConfigBuilder;
import uk.gov.hmcts.ccd.sdk.api.Event;
import uk.gov.hmcts.ccd.sdk.api.EventPayload;
import uk.gov.hmcts.ccd.sdk.api.callback.SubmitResponse;
import uk.gov.hmcts.reform.pt.ccd.common.PageBuilder;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.State;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole;
import uk.gov.hmcts.reform.pt.ccd.pages.caserequestvettinginfo.CaseRequestVettingInformationConfigurer;

import static uk.gov.hmcts.ccd.sdk.api.Permission.CRU;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.PT_CASE_WORKER;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.CASE_ISSUED;
import static uk.gov.hmcts.reform.pt.ccd.domain.State.CASE_PROGRESSION;
import static uk.gov.hmcts.reform.pt.ccd.event.EventId.CASE_REQUEST_VETTING_INFORMATION;

@Component
@RequiredArgsConstructor
public class CaseRequestVettingInformation implements CCDConfig<PTCase, State, UserRole> {
    private final CaseRequestVettingInformationConfigurer pageConfigurer;

    @Override
    public void configureDecentralised(DecentralisedConfigBuilder<PTCase, State, UserRole> configBuilder) {
        Event.EventBuilder<PTCase, UserRole, State> eventBuilder = configBuilder
            .decentralisedEvent(CASE_REQUEST_VETTING_INFORMATION.getId(), this::submit)
            .forStateTransition(CASE_ISSUED, CASE_PROGRESSION)
            .name(CASE_REQUEST_VETTING_INFORMATION.getName())
            .description(CASE_REQUEST_VETTING_INFORMATION.getName())
            .grant(CRU, PT_CASE_WORKER)
            .showSummary()
            .endButtonLabel("Submit");
        pageConfigurer.configurePages(new PageBuilder(eventBuilder));
    }

    private SubmitResponse<State> submit(EventPayload<PTCase, State> payload) {
        // todo HDPD-1364 - send email
        return SubmitResponse.<State>builder()
            .confirmationBody(buildConfirmationBody(payload.caseReference(), payload.caseData()))
            .build();
    }

    private String buildConfirmationBody(long caseReference, PTCase ptCase) {
        return
            """
            <div class="govuk-panel govuk-panel--confirmation">
                <h1 class="govuk-panel__title">Vetting information requested</h1>
                <div class="govuk-panel__body govuk-!-font-size-24">Case number: %s</div>
                <div class="govuk-panel__body govuk-!-font-size-24">%s</div>
                <div class="govuk-panel__body govuk-!-font-size-24">%s</div>
            </div>

            <p class="govuk-body">
                We have sent a notification to the applicant, asking them to provide the missing information.
            </p>

            <h3 class="govuk-heading-s">What happens next</h3>
            <p class="govuk-body govuk-!-margin-bottom-6">
                We will tell the respondent that the applicant has applied for a market rent determination.
            </p>

            <a class="govuk-link govuk-!-margin-bottom-6 govuk-!-font-size-19"
               href="/cases/case-details/%s"
               data-module="govuk-button">
               Return to my tasks
            </a>
            """.formatted(
                caseReference,
                ptCase.getFormattedPropertyAddress(),
                ptCase.getFormattedCaseName(),
                caseReference);
    }
}
