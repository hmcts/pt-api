package uk.gov.hmcts.reform.pt.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class EnumSetConverter<E extends Enum<E>> implements AttributeConverter<Set<E>, String> {
    private static final String COMMA_DELIMITER = ",";

    private final Class<E> type;

    @Override
    public String convertToDatabaseColumn(Set<E> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }

        return attribute.stream()
            .map(Enum::name)
            .collect(Collectors.joining(COMMA_DELIMITER));
    }

    @Override
    public Set<E> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return EnumSet.noneOf(type);
        }

        return Arrays.stream(dbData.split(COMMA_DELIMITER))
            .map(String::trim)
            .map(s -> Enum.valueOf(type, s))
            .collect(Collectors.toSet());
    }
}
