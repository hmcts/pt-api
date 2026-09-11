package uk.gov.hmcts.reform.pt.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class NullSafeSetterTest {
    @Test
    @DisplayName("Should invoke setter when value is not null")
    void setIfNotNullWhenValuePresent() {
        StringHolder target = new StringHolder("initial");

        NullSafeSetter.setIfNotNull("updated", target::setValue);

        assertThat(target.getValue()).isEqualTo("updated");
    }

    @Test
    @DisplayName("Should not invoke setter when value is null")
    void setIfNotNullWhenValueNull() {
        StringHolder target = new StringHolder("initial");

        NullSafeSetter.setIfNotNull(null, target::setValue);

        assertThat(target.getValue()).isEqualTo("initial");
    }

    @Test
    @DisplayName("Should invoke setter with start of day LocalDateTime when LocalDate value is not null")
    void setIfNotNullLocalDateWhenValuePresent() {
        DateTimeHolder target = new DateTimeHolder(LocalDateTime.of(2020, 1, 1, 12, 0));
        LocalDate date = LocalDate.of(2025, 5, 20);

        NullSafeSetter.setIfNotNull(date, target::setDateTime);

        assertThat(target.getDateTime()).isEqualTo(LocalDateTime.of(2025, 5, 20, 0, 0));
    }

    @Test
    @DisplayName("Should not invoke setter when LocalDate value is null")
    void setIfNotNullLocalDateWhenValueNull() {
        LocalDateTime initialDateTime = LocalDateTime.of(2020, 1, 1, 12, 0);
        DateTimeHolder target = new DateTimeHolder(initialDateTime);
        LocalDate date = null;

        NullSafeSetter.setIfNotNull(date, target::setDateTime);

        assertThat(target.getDateTime()).isEqualTo(initialDateTime);
    }

    @Data
    @AllArgsConstructor
    private static final class StringHolder {
        private String value;
    }

    @Data
    @AllArgsConstructor
    private static final class DateTimeHolder {
        private LocalDateTime dateTime;
    }
}
