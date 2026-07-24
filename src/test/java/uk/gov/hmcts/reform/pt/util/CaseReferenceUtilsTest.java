package uk.gov.hmcts.reform.pt.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CaseReferenceUtilsTest {

    @Test
    void shouldReturnTrueForValid16DigitCaseReference() {
        long caseReference = 1234567890123456L;

        boolean result = CaseReferenceUtils.isValidCaseReference(caseReference);

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnTrueForMinimumValidCaseReference() {
        long caseReference = 1_000_000_000_000_000L;

        boolean result = CaseReferenceUtils.isValidCaseReference(caseReference);

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnTrueForMaximumValidCaseReference() {
        long caseReference = 9_999_999_999_999_999L;

        boolean result = CaseReferenceUtils.isValidCaseReference(caseReference);

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForCaseReferenceWithLessThan16Digits() {
        long caseReference = 999_999_999_999_999L;

        boolean result = CaseReferenceUtils.isValidCaseReference(caseReference);

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseForCaseReferenceWithMoreThan16Digits() {
        long caseReference = 10_000_000_000_000_000L;

        boolean result = CaseReferenceUtils.isValidCaseReference(caseReference);

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseForZeroCaseReference() {
        boolean result = CaseReferenceUtils.isValidCaseReference(0L);

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseForNegativeCaseReference() {
        boolean result = CaseReferenceUtils.isValidCaseReference(-1234567890123456L);

        assertThat(result).isFalse();
    }
}
