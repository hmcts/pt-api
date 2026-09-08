package uk.gov.hmcts.reform.pt.ccd.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.hmcts.ccd.sdk.type.ListValue;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PTCase reaches these classes through @JsonUnwrapped(prefix = ...), and Jackson applies that
 * prefix recursively into a nested complex object. A single UploadedDocument field therefore
 * deserialises with an empty Document unless it names UploadedDocumentDeserializer, which reads
 * the value without the prefix transformer. Collections are unaffected.
 *
 * <p>Losing the annotation is silent until an upload fails with a null Document, so assert it is
 * present on every field that needs it.
 */
class DocumentFieldAnnotationTest {

    @ParameterizedTest
    @ValueSource(classes = {PropertyDetails.class, NoticeOfRentIncreaseDetails.class})
    @DisplayName("Every single document field should name the deserialiser")
    void singleDocumentFieldsShouldNameTheDeserialiser(Class<?> sliceClass) {
        List<Field> singleDocumentFields = Arrays.stream(sliceClass.getDeclaredFields())
            .filter(field -> field.getType() == UploadedDocument.class)
            .toList();

        assertThat(singleDocumentFields)
            .describedAs("no single document fields found on %s, so this test proves nothing",
                sliceClass.getSimpleName())
            .isNotEmpty();

        assertThat(singleDocumentFields)
            .allSatisfy(field -> {
                JsonDeserialize annotation = field.getAnnotation(JsonDeserialize.class);

                assertThat(annotation)
                    .describedAs("%s.%s is missing @JsonDeserialize(using = %s.class)",
                        sliceClass.getSimpleName(), field.getName(),
                        UploadedDocumentDeserializer.class.getSimpleName())
                    .isNotNull();

                assertThat(annotation.using())
                    .describedAs("%s.%s names the wrong deserialiser",
                        sliceClass.getSimpleName(), field.getName())
                    .isEqualTo(UploadedDocumentDeserializer.class);
            });
    }

    @ParameterizedTest
    @ValueSource(classes = {PropertyDetails.class, NoticeOfRentIncreaseDetails.class})
    @DisplayName("A document collection should be modelled as CCD serialises it")
    void documentCollectionsShouldUseListValue(Class<?> sliceClass) {
        Arrays.stream(sliceClass.getDeclaredFields())
            .filter(field -> List.class.isAssignableFrom(field.getType()))
            .filter(field -> holdsUploadedDocuments(field.getGenericType()))
            .forEach(field -> assertThat(itemTypeOf(field.getGenericType()))
                .describedAs("%s.%s must be List<ListValue<UploadedDocument>>; CCD serialises a "
                        + "collection as [{id, value}] and a plain list cannot read that back",
                    sliceClass.getSimpleName(), field.getName())
                .isEqualTo(ListValue.class));
    }

    private static boolean holdsUploadedDocuments(Type genericType) {
        return genericType.getTypeName().contains(UploadedDocument.class.getName());
    }

    private static Type itemTypeOf(Type genericType) {
        Type item = ((ParameterizedType) genericType).getActualTypeArguments()[0];
        return item instanceof ParameterizedType parameterized ? parameterized.getRawType() : item;
    }
}
