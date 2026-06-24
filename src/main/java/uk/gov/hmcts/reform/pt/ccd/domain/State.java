package uk.gov.hmcts.reform.pt.ccd.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.CaseworkerReadAccess;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.CitizenAccess;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.ClaimantAccess;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.DefendantAccess;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.GlobalSearchAccess;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.InternalCaseFlagAccess;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.RasValidationAccess;

/**
 * All possible PCS case states.
 * Converted into CCD states.
 */
@RequiredArgsConstructor
@Getter
public enum State {

    @CCD(
        label = "Awaiting Submission to HMCTS",
        access = {ClaimantAccess.class, CitizenAccess.class, RasValidationAccess.class, CaseworkerReadAccess.class },
        hint = "${caseTitleMarkdown}"
    )
    AWAITING_SUBMISSION_TO_HMCTS,

    @CCD(
        label = "Pending Case Issued",
        access = {ClaimantAccess.class, CitizenAccess.class,  RasValidationAccess.class,
            InternalCaseFlagAccess.class, GlobalSearchAccess.class, CaseworkerReadAccess.class},
        hint = "${caseTitleMarkdown}"
    )
    PENDING_CASE_ISSUED,

    @CCD(
        label = "Case Issued",
        access = {CaseworkerReadAccess.class, ClaimantAccess.class, DefendantAccess.class, RasValidationAccess.class,
            GlobalSearchAccess.class},
        hint = "${caseTitleMarkdown}"
    )
    CASE_ISSUED
}

