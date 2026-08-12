package uk.gov.hmcts.reform.pt.ccd.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.api.HasLabel;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.CaseworkerReadAccess;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.ClaimantAccess;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.GlobalSearchAccess;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.InternalCaseFlagAccess;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.RasValidationAccess;

/**
 * All possible PT case states.
 * Converted into CCD states.
 */
@RequiredArgsConstructor
@Getter
public enum State implements HasLabel {

    @CCD(
        label = "Awaiting Submission to HMCTS",
        access = {ClaimantAccess.class, RasValidationAccess.class, CaseworkerReadAccess.class },
        hint = "${caseTitleMarkdown}"
    )
    AWAITING_SUBMISSION_TO_HMCTS("Awaiting Submission to HMCTS"),

    @CCD(
        label = "Pending Case Issued",
        access = {ClaimantAccess.class,  RasValidationAccess.class,
            InternalCaseFlagAccess.class, GlobalSearchAccess.class, CaseworkerReadAccess.class},
        hint = "${caseTitleMarkdown}"
    )
    PENDING_CASE_ISSUED("Pending Case Issued"),

    @CCD(
        label = "Case Issued",
        access = {CaseworkerReadAccess.class, ClaimantAccess.class, RasValidationAccess.class,
            GlobalSearchAccess.class},
        hint = "${caseTitleMarkdown}"
    )
    CASE_ISSUED("Case Issued"),

    @CCD(
        label = "Draft Discarded",
        access = {CaseworkerReadAccess.class, ClaimantAccess.class, RasValidationAccess.class,
            GlobalSearchAccess.class}
    )
    DRAFT_DISCARDED("Draft Discarded"),

    @CCD(
        label = "Requested for Deletion",
        access = {CaseworkerReadAccess.class, ClaimantAccess.class, RasValidationAccess.class,
            GlobalSearchAccess.class}
    )
    REQUESTED_FOR_DELETION("Requested for Deletion"),

    @CCD(
        label = "Case progression",
        access = {CaseworkerReadAccess.class, ClaimantAccess.class, RasValidationAccess.class,
            GlobalSearchAccess.class}
    )
    CASE_PROGRESSION("Case progression"),

    @CCD(
        label = "Awaiting Listing",
        access = {CaseworkerReadAccess.class, ClaimantAccess.class, RasValidationAccess.class,
            GlobalSearchAccess.class}
    )
    HEARING_READINESS("Awaiting Listing"),

    @CCD(
        label = "Awaiting Hearing",
        access = {CaseworkerReadAccess.class, ClaimantAccess.class, RasValidationAccess.class,
            GlobalSearchAccess.class}
    )
    PREPARE_FOR_HEARING_CONDUCT_HEARING("Awaiting Hearing"),

    @CCD(
        label = "Awaiting Judgement",
        access = {CaseworkerReadAccess.class, ClaimantAccess.class, RasValidationAccess.class,
            GlobalSearchAccess.class}
    )
    AWAITING_JUDGMENT("Awaiting Judgement"),

    @CCD(
        label = "Closed",
        access = {CaseworkerReadAccess.class, ClaimantAccess.class, RasValidationAccess.class,
            GlobalSearchAccess.class}
    )
    CLOSED("Closed"),

    @CCD(
        label = "Case Stayed",
        access = {CaseworkerReadAccess.class, ClaimantAccess.class, RasValidationAccess.class,
            GlobalSearchAccess.class}
    )
    CASE_STAYED("Case Stayed");

    private final String label;
}

