package uk.gov.hmcts.reform.pt.ccd.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.hmcts.ccd.sdk.type.ListValue;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentCollectionModellingTest {

    @ParameterizedTest
    @ValueSource(classes = {PropertyDetails.class, NoticeOfRentIncreaseDetails.class})
    @DisplayName("A document collection should be modelled as CCD serialises it")
    void documentCollectionsShouldUseListValue(Class<?> sliceClass) {
        Arrays.stream(sliceClass.getDeclaredFields())
            .filter(field -> List.class.isAssignableFrom(field.getType()))
            .filter(field -> holdsUploadedDocuments(field.getGenericType()))
            .forEach(field -> assertThat(itemTypeOf(field.getGenericType()))
                .describedAs("%s.%s must be List<ListValue<UploadedDocument>>", sliceClass.getSimpleName(),
                    field.getName())
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
