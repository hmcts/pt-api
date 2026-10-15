package uk.gov.hmcts.reform.pt.ccd.event.citizen;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.hmcts.ccd.data.casedetails.SecurityClassification;
import uk.gov.hmcts.ccd.decentralised.dto.DecentralisedCaseEvent;
import uk.gov.hmcts.ccd.decentralised.dto.DecentralisedEventDetails;
import uk.gov.hmcts.ccd.decentralised.dto.DecentralisedSubmitEventResponse;
import uk.gov.hmcts.ccd.domain.model.definition.CaseDetails;
import uk.gov.hmcts.ccd.sdk.impl.CaseSubmissionService;
import uk.gov.hmcts.ccd.sdk.impl.IdamService;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;
import uk.gov.hmcts.reform.pt.config.AbstractPostgresContainerIT;
import uk.gov.hmcts.reform.pt.entity.DocumentEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.repository.DocumentRepository;
import uk.gov.hmcts.reform.pt.repository.PTCaseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class CitizenUploadDocumentConcurrencyIT extends AbstractPostgresContainerIT {

    private static final long MB = 1024L * 1024;
    private static final String STATE = "AWAITING_SUBMISSION_TO_HMCTS";

    @Autowired
    private CaseSubmissionService caseSubmissionService;
    @Autowired
    private PTCaseRepository ptCaseRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IdamService idamService;

    private long caseReference;

    @BeforeEach
    void setUp() {
        UserInfo user = UserInfo.builder().uid(UUID.randomUUID().toString()).givenName("Test").familyName("Citizen")
            .roles(List.of("citizen")).build();
        when(idamService.retrieveUser(anyString())).thenReturn(new IdamService.User("token", user));

        caseReference = 1_000_000_000_000_000L + ThreadLocalRandom.current().nextLong(999_999_999_999_999L);
        ptCaseRepository.save(PTCaseEntity.builder().caseReference(caseReference).build());
        jdbcTemplate.update(
            "insert into ccd.case_data (id, reference, version, security_classification, jurisdiction, case_type_id,"
                + " state, data) values (?, ?, 1, 'PUBLIC'::ccd.securityclassification, 'PT', 'PT', ?, '{}'::jsonb)",
            caseReference, caseReference, STATE
        );
    }

    @Test
    void concurrentUploadsCannotTakeAMultiDocumentFieldOverItsTotalLimit() throws Exception {
        List<DecentralisedSubmitEventResponse> responses = submitConcurrently(5, i -> Map.of(
            "propertyDetails", Map.of("repairsEvidenceDocuments", List.of(Map.of(
                "id", UUID.randomUUID().toString(),
                "value", document("repairs-" + i, 100 * MB)
            )))
        ));

        assertThat(responses).filteredOn(r -> r.getErrors() == null || r.getErrors().isEmpty()).hasSize(3);
        assertThat(responses).filteredOn(r -> r.getErrors() != null && r.getErrors().contains("totalTooLarge"))
            .hasSize(2);
        assertThat(documentRepository.findAllByPtCaseCaseReference(caseReference))
            .hasSize(3)
            .extracting(DocumentEntity::getSize)
            .containsOnly(100 * MB);
    }

    @Test
    void concurrentUploadsCannotOverwriteASingleDocumentField() throws Exception {
        List<DecentralisedSubmitEventResponse> responses = submitConcurrently(4, i -> Map.of(
            "tenancyAgreementDetails", Map.of("tenancyAgreementDocument", document("tenancy-" + i, MB))
        ));

        assertThat(responses).filteredOn(r -> r.getErrors() == null || r.getErrors().isEmpty()).hasSize(1);
        assertThat(responses).filteredOn(r -> r.getErrors() != null && r.getErrors().contains("removeFileFirst"))
            .hasSize(3);

        List<DocumentEntity> stored = documentRepository.findAllByPtCaseCaseReference(caseReference);
        assertThat(stored).hasSize(1);
    }

    private interface Payload {
        Map<String, Object> forUpload(int index);
    }

    private List<DecentralisedSubmitEventResponse> submitConcurrently(int uploads, Payload payload)
        throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(uploads);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<DecentralisedSubmitEventResponse>> futures = new ArrayList<>();
        for (int i = 0; i < uploads; i++) {
            DecentralisedCaseEvent event = event(payload.forUpload(i));
            futures.add(pool.submit(() -> {
                start.await();
                return caseSubmissionService.submit(event, "Bearer token", UUID.randomUUID());
            }));
        }
        start.countDown();
        List<DecentralisedSubmitEventResponse> responses = new ArrayList<>();
        for (Future<DecentralisedSubmitEventResponse> future : futures) {
            responses.add(future.get());
        }
        pool.shutdown();
        return responses;
    }

    private Map<String, Object> document(String name, long size) {
        return Map.of(
            "document", Map.of(
                "document_url", "http://dm-store/documents/" + name + "-" + UUID.randomUUID(),
                "document_binary_url", "http://dm-store/documents/" + name + "/binary",
                "document_filename", name + ".mp4"
            ),
            "contentType", "video/mp4",
            "sizeInBytes", size
        );
    }

    private DecentralisedCaseEvent event(Map<String, Object> data) {
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setReference(caseReference);
        caseDetails.setJurisdiction("PT");
        caseDetails.setCaseTypeId("PT");
        caseDetails.setState(STATE);
        caseDetails.setVersion(1);
        caseDetails.setSecurityClassification(SecurityClassification.PUBLIC);
        caseDetails.setData(objectMapper.convertValue(data, objectMapper.getTypeFactory()
            .constructMapType(Map.class, String.class, JsonNode.class)));

        DecentralisedEventDetails eventDetails = DecentralisedEventDetails.builder()
            .caseType("PT")
            .eventId("citizen-upload-document")
            .eventName("Upload document")
            .summary("")
            .description("")
            .build();

        return DecentralisedCaseEvent.builder()
            .caseDetails(caseDetails)
            .eventDetails(eventDetails)
            .internalCaseId(caseReference)
            .build();
    }
}
