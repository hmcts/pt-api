package uk.gov.hmcts.reform.pt.notify.template;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmailTemplate {
    TEST_TEMPLATE("test-template");

    private final String templateKey;
}
