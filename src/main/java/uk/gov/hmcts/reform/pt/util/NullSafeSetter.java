package uk.gov.hmcts.reform.pt.util;

import lombok.experimental.UtilityClass;

import java.util.function.Consumer;

@UtilityClass
public final class NullSafeSetter {
    public static <T> void setIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
