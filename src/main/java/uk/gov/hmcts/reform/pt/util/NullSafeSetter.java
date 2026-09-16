package uk.gov.hmcts.reform.pt.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Consumer;

public final class NullSafeSetter {
    public static <T> void setIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    public static void setIfNotNull(LocalDate value, Consumer<LocalDateTime> setter) {
        if (value != null) {
            setter.accept(value.atStartOfDay());
        }
    }
}
