package uk.gov.hmcts.reform.pt.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PartyAccessCodeEntityTest {

    private static final LocalDateTime EXPIRES_AT = LocalDateTime.of(2026, 11, 11, 10, 0);

    private final PartyAccessCodeEntity entity = PartyAccessCodeEntity.builder().expiresAt(EXPIRES_AT).build();

    @Test
    void shouldNotBeExpiredBeforeExpiryTime() {
        assertThat(entity.isExpired(EXPIRES_AT.minusSeconds(1))).isFalse();
    }

    @Test
    void shouldBeExpiredAtExactExpiryTime() {
        assertThat(entity.isExpired(EXPIRES_AT)).isTrue();
    }

    @Test
    void shouldBeExpiredAfterExpiryTime() {
        assertThat(entity.isExpired(EXPIRES_AT.plusDays(1))).isTrue();
    }
}
