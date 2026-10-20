package uk.gov.hmcts.reform.pt.ccd.common;

import java.beans.Introspector;

public abstract class CcdPage {
    protected String getPageKey() {
        String pageKey = getClass().getSimpleName().replaceFirst("Page$", "");
        return Introspector.decapitalize(pageKey);
    }

    protected String deriveSummaryKey() {
        return deriveKey("summary");
    }

    protected String deriveLineSeparatorKey() {
        return deriveKey("lineSeparator");
    }

    protected String deriveKey(String labelName) {
        return getPageKey() + "-" + labelName;
    }
}
