package uk.gov.hmcts.reform.pt.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class CaseReferenceUtilsTest {
    @ValueSource(longs = {
        1234567890123456L,        // valid 16-digit case reference
        1_000_000_000_000_000L,   // minimum valid case reference
        9_999_999_999_999_999L    // maximum valid case reference
    })
    @ParameterizedTest(name = "shouldReturnTrueForValidCaseReference: {0}")
    void shouldReturnTrueForValidCaseReference(long caseReference) {
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
