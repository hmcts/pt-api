package uk.gov.hmcts.reform.pt.ccd.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Reads an {@link UploadedDocument} that sits inside a {@code @JsonUnwrapped(prefix = ...)} slice.
 *
 * <p>Looks like a no-op, and is not — do not remove it. Jackson applies an unwrapped slice's prefix
 * transformer recursively, so it expects every field name inside the slice to carry the prefix,
 * including the ones nested within a complex value. CCD sends the document as
 * {@code propertyDetailsFloorPlanDocument: {"documentType": ..., "document": {...}}}; Jackson strips
 * the prefix from the outer key, then looks for {@code propertyDetailsDocumentType} and
 * {@code propertyDetailsDocument} inside, finds neither, and drops them. The result is a
 * constructed but completely empty UploadedDocument, which then fails downstream on a null document
 * rather than anywhere near the real cause.
 *
 * <p>A custom deserialiser does not override {@code unwrappingDeserializer}, so the transformer is
 * not propagated into it and the nested fields bind normally. Collections are unaffected, which is
 * why {@code roomsDocuments} does not need this.
 */
public class UploadedDocumentDeserializer extends JsonDeserializer<UploadedDocument> {

    @Override
    public UploadedDocument deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return parser.readValueAs(UploadedDocument.class);
    }
}
