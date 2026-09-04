package uk.gov.hmcts.reform.pt.ccd.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ccd.sdk.type.Document;

import static org.assertj.core.api.Assertions.assertThat;

class UploadedDocumentDeserializerTest {

    private final ObjectMapper mapper = new ObjectMapper()
        .registerModule(new ParameterNamesModule())
        .registerModule(new JavaTimeModule());

    private UploadedDocument uploadedDocument(DocumentType type) {
        return UploadedDocument.builder()
            .documentType(type)
            .document(Document.builder()
                .url("http://cdam/cases/documents/abc")
                .binaryUrl("http://cdam/cases/documents/abc/binary")
                .filename("floor-plan.pdf")
                .build())
            .contentType("application/pdf")
            .sizeInBytes(1024L)
            .build();
    }

    @Test
    @DisplayName("Should read back a document held in a @JsonUnwrapped slice")
    void shouldRoundTripADocumentInsideAnUnwrappedSlice() throws Exception {
        PTCase original = PTCase.builder()
            .propertyDetails(PropertyDetails.builder()
                .floorPlanDocument(uploadedDocument(DocumentType.FLOOR_PLAN))
                .build())
            .build();

        PTCase result = mapper.readValue(mapper.writeValueAsString(original), PTCase.class);

        UploadedDocument floorPlan = result.getPropertyDetails().getFloorPlanDocument();
        assertThat(floorPlan).isNotNull();
        assertThat(floorPlan.getDocumentType()).isEqualTo(DocumentType.FLOOR_PLAN);
        assertThat(floorPlan.getContentType()).isEqualTo("application/pdf");
        assertThat(floorPlan.getSizeInBytes()).isEqualTo(1024L);
        // The one that regresses without the deserialiser: Jackson applies the slice's prefix
        // transformer recursively, drops every unprefixed nested field, and leaves an empty object.
        assertThat(floorPlan.getDocument()).isNotNull();
        assertThat(floorPlan.getDocument().getUrl()).isEqualTo("http://cdam/cases/documents/abc");
    }

    @Test
    @DisplayName("Should read back a document on the notice of rent increase slice")
    void shouldRoundTripANoticeDocument() throws Exception {
        PTCase original = PTCase.builder()
            .noticeOfRentIncreaseDetails(NoticeOfRentIncreaseDetails.builder()
                .landlordNoticeProposingNewRentDocument(uploadedDocument(DocumentType.NEW_RENT_INCREASE_NOTICE))
                .build())
            .build();

        PTCase result = mapper.readValue(mapper.writeValueAsString(original), PTCase.class);

        UploadedDocument notice = result.getNoticeOfRentIncreaseDetails()
            .getLandlordNoticeProposingNewRentDocument();
        assertThat(notice).isNotNull();
        assertThat(notice.getDocument()).isNotNull();
        assertThat(notice.getDocument().getUrl()).isEqualTo("http://cdam/cases/documents/abc");
    }

    @Test
    @DisplayName("Should leave an absent document as null rather than an empty object")
    void shouldLeaveAnAbsentDocumentNull() throws Exception {
        PTCase result = mapper.readValue("{\"applicantFirstName\":\"Jane\"}", PTCase.class);

        assertThat(result.getPropertyDetails().getFloorPlanDocument()).isNull();
    }

    @Test
    @DisplayName("Should read back a document collection in CCD's id/value shape")
    void shouldRoundTripADocumentCollection() throws Exception {
        PTCase original = PTCase.builder()
            .propertyDetails(PropertyDetails.builder()
                .roomsDocuments(java.util.List.of(
                    new uk.gov.hmcts.ccd.sdk.type.ListValue<>("1", uploadedDocument(DocumentType.PROPERTY_ROOMS))))
                .build())
            .build();

        String json = mapper.writeValueAsString(original);
        // CCD serialises a Collection as [{id, value}] — a plain List<UploadedDocument> cannot read it back.
        assertThat(json).contains("\"id\":\"1\"").contains("\"value\":");

        var rooms = mapper.readValue(json, PTCase.class).getPropertyDetails().getRoomsDocuments();

        assertThat(rooms).hasSize(1);
        assertThat(rooms.get(0).getValue().getDocument()).isNotNull();
        assertThat(rooms.get(0).getValue().getDocument().getUrl()).isEqualTo("http://cdam/cases/documents/abc");
    }
}
