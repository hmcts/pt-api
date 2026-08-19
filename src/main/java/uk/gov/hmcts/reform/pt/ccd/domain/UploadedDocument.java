package uk.gov.hmcts.reform.pt.ccd.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.hmcts.ccd.sdk.api.CCD;
import uk.gov.hmcts.ccd.sdk.type.Document;
import uk.gov.hmcts.ccd.sdk.type.FieldType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadedDocument {

    @CCD(ignore = true)
    private static final String ACCEPT_TYPES = ".doc,.docx,.xls,.xlsx,.ppt,.pptx,.pdf,.rtf,.txt,.csv,"
        + ".jpg,.jpeg,.png,.bmp,.tif,.tiff";

    @CCD(
        label = "Type of document",
        typeOverride = FieldType.FixedList,
        typeParameterOverride = "DocumentType"
    )
    private DocumentType documentType;

    @CCD(
        label = "Document",
        // Note this regex attribute is not actually interpreted as a regex for the Document type
        regex = ACCEPT_TYPES
    )
    private Document document;

    @CCD
    private String contentType;

    @CCD
    private Long sizeInBytes;

}
