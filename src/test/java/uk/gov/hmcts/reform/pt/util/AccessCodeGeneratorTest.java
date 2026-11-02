package uk.gov.hmcts.reform.pt.util;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AccessCodeGeneratorTest {

    private static final String ALLOWED_CHARS = "ABCDEFGHJKLMNPRSTVWXYZ23456789";

    private final AccessCodeGenerator generator = new AccessCodeGenerator();

    @RepeatedTest(20)
    void shouldGenerateTwelveCharactersFromAllowedAlphabet() {
        String code = generator.generate();

        assertThat(code).hasSize(12);
        assertThat(code.chars()).allMatch(c -> ALLOWED_CHARS.indexOf(c) >= 0);
    }

    @Test
    void shouldNotContainAmbiguousCharacters() {
        for (int i = 0; i < 100; i++) {
            assertThat(generator.generate()).doesNotContain("0", "1", "I", "O", "Q", "U");
        }
    }

    @Test
    void shouldGenerateDifferentCodes() {
        Set<String> codes = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            codes.add(generator.generate());
        }
        assertThat(codes).hasSize(100);
    }
}
