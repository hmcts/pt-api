package uk.gov.hmcts.reform.pt.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

@UtilityClass
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

    public static <T> void setIfElseNull(BooleanSupplier condition, Supplier<T> value, Consumer<T> setter) {
        if (condition.getAsBoolean()) {
            setIfNotNull(value.get(), setter);
        } else {
            setter.accept(null);
        }
    }
}
