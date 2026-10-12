package uk.gov.hmcts.reform.pt.notify.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NotificationStatusTest {
    @ParameterizedTest
    @EnumSource(NotificationStatus.class)
    void fromStringReturnMatchingEnum(NotificationStatus status) {
        assertThat(NotificationStatus.fromString(status.getValue())).isEqualTo(status);
        assertThat(NotificationStatus.fromString(status.getValue().toUpperCase())).isEqualTo(status);
    }

    @Test
    void fromStringThrowExceptionForUnknownStatus() {
        assertThatThrownBy(() -> NotificationStatus.fromString("invalid-status"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Unknown status: invalid-status");
    }
}
