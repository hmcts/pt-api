package uk.gov.hmcts.reform.pt.util;

public class CaseReferenceUtils {

    public static boolean isValidCaseReference(long caseReference) {
        // case reference must be 16 characters long
        return caseReference >= 1_000_000_000_000_000L
            && caseReference <= 9_999_999_999_999_999L;
    }
}
