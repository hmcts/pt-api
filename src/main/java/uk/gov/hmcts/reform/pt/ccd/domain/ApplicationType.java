package uk.gov.hmcts.reform.pt.ccd.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.hmcts.ccd.sdk.api.HasLabel;

@Getter
@AllArgsConstructor
public enum ApplicationType implements HasLabel {
    CHALLENGE_RENT_INCREASE("Challenge Rent Increase"),
    CHALLENGE_NOTICE_LEGAL_VALIDITY("Challenge Notice Legal Validity"),
    CHALLENGE_EXCESSIVE_RENT("Challenge Excessive Rent");

    private final String label;
}
