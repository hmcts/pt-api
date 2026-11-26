package uk.gov.hmcts.reform.pt.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyDetailsDocumentAssociationTest {
    @Test
    @DisplayName("Links document to case property details on both sides")
    void shouldLinkDocumentToCasePropertyDetailsOnBothSides() {
        CasePropertyDetailsEntity details = CasePropertyDetailsEntity.builder().build();
        DocumentEntity document = DocumentEntity.builder().build();

        details.addDocument(document);
        details.addDocument(document);

        assertThat(details.getDocuments()).containsExactly(document);
        assertThat(document.getCasePropertyDetails()).containsExactly(details);
    }

    @Test
    @DisplayName("Links document to respondent property details on both sides")
    void shouldLinkDocumentToRespondentPropertyDetailsOnBothSides() {
        RespondentPropertyDetailsEntity details = RespondentPropertyDetailsEntity.builder().build();
        DocumentEntity document = DocumentEntity.builder().build();

        details.addDocument(document);
        details.addDocument(document);

        assertThat(details.getDocuments()).containsExactly(document);
        assertThat(document.getRespondentPropertyDetails()).containsExactly(details);
    }
}
