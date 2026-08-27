package uk.gov.hmcts.reform.pt.ccd.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.gov.hmcts.ccd.sdk.api.HasLabel;

@Getter
@RequiredArgsConstructor
public enum CaseFileCategory implements HasLabel {

    UNCATEGORISED_DOCUMENTS("uncategorisedDocuments", "Uncategorised documents", 1);

    private final String id;
    private final String label;
    private final int displayOrder;
}
