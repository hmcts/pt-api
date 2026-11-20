package uk.gov.hmcts.reform.pt.entity.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.hmcts.reform.pt.ccd.domain.VettingInformationType;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VettingInformationTypeSetConverterTest {

    private VettingInformationTypeSetConverter converter;

    @BeforeEach
    void setUp() {
        converter = new VettingInformationTypeSetConverter();
    }

    @Test
    @DisplayName("Should convert null set to null database column")
    void shouldConvertNullSetToNullDatabaseColumn() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
    }

    @Test
    @DisplayName("Should convert empty set to null database column")
    void shouldConvertEmptySetToNullDatabaseColumn() {
        assertThat(converter.convertToDatabaseColumn(Collections.emptySet())).isNull();
    }

    @Test
    @DisplayName("Should convert single enum set to database column")
    void shouldConvertSingleEnumSetToDatabaseColumn() {
        String result = converter.convertToDatabaseColumn(Set.of(VettingInformationType.SECTION_13_NOTICE));

        assertThat(result).isEqualTo("SECTION_13_NOTICE");
    }

    @Test
    @DisplayName("Should convert multiple enum set to comma-separated database column")
    void shouldConvertMultipleEnumSetToCommaSeparatedDatabaseColumn() {
        Set<VettingInformationType> types = EnumSet.of(
            VettingInformationType.SECTION_13_NOTICE,
            VettingInformationType.TENANCY_AGREEMENT
        );

        String result = converter.convertToDatabaseColumn(types);

        assertThat(result).isIn("SECTION_13_NOTICE,TENANCY_AGREEMENT", "TENANCY_AGREEMENT,SECTION_13_NOTICE");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Should convert null or blank database column to empty EnumSet")
    void shouldConvertNullOrBlankColumnToEmptySet(String dbData) {
        Set<VettingInformationType> result = converter.convertToEntityAttribute(dbData);

        assertThat(result)
            .isNotNull()
            .isEmpty();
    }

    @Test
    @DisplayName("Should convert single value database column to entity attribute set")
    void shouldConvertSingleValueColumnToEntityAttribute() {
        Set<VettingInformationType> result = converter.convertToEntityAttribute("SECTION_13_NOTICE");

        assertThat(result).containsExactly(VettingInformationType.SECTION_13_NOTICE);
    }

    @Test
    @DisplayName("Should convert comma-separated database column with whitespace to entity attribute set")
    void shouldConvertCommaSeparatedColumnToEntityAttribute() {
        Set<VettingInformationType> result = converter.convertToEntityAttribute(
            "SECTION_13_NOTICE, TENANCY_AGREEMENT, OTHER"
        );

        assertThat(result).containsExactlyInAnyOrder(
            VettingInformationType.SECTION_13_NOTICE,
            VettingInformationType.TENANCY_AGREEMENT,
            VettingInformationType.OTHER
        );
    }
}
