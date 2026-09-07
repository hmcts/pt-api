package uk.gov.hmcts.reform.pt.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static final class StringHolder {
        private String value;
    }
}
