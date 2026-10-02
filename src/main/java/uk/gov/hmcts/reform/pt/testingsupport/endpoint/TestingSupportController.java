package uk.gov.hmcts.reform.pt.testingsupport.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.pt.controllers.advice.ErrorResponse;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.notify.model.NotificationRequest;
import uk.gov.hmcts.reform.pt.notify.model.NotificationResponse;
import uk.gov.hmcts.reform.pt.notify.service.NotificationService;
import uk.gov.hmcts.reform.pt.notify.template.EmailTemplate;
import uk.gov.hmcts.reform.pt.repository.PTCaseRepository;
import uk.gov.hmcts.reform.pt.service.PTCaseService;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/testing-support")
@ConditionalOnProperty(name = "testing-support.enabled", havingValue = "true")
@Tag(name = "Testing Support")
public class TestingSupportController {

    private final PTCaseService ptCaseService;
    private final PTCaseRepository ptCaseRepository;
    private final NotificationService notificationService;

    private static final long TEST_NOTIFICATION_CASE_REFERENCE = 1234123412341234L;

    @DeleteMapping("/cases/{caseReference}")
    @PreAuthorize("hasAuthority('pt-system-update')")
    @Operation(
        summary = "Delete a case",
        description = "Deletes a case and all of its related data. For test data cleanup only. "
            + "Requires a system user token.",
        security = {
            @SecurityRequirement(name = "AuthorizationToken"),
            @SecurityRequirement(name = "ServiceAuthorization")
        }
    )
    @ApiResponse(responseCode = "204", description = "Case deleted successfully")
    @ApiResponse(responseCode = "401", description = "Missing or invalid access token")
    @ApiResponse(responseCode = "403", description = "Caller does not have the correct roles assigned")
    @ApiResponse(responseCode = "404", description = "Case not found")
    public ResponseEntity<Object> deleteCase(
        @Parameter(description = "The case reference to delete", required = true)
        @PathVariable long caseReference
    ) {
        try {
            ptCaseService.deleteCase(caseReference);
        } catch (CaseNotFoundException e) {
            log.error("Case not found", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/notify-test")
    @Operation(
        summary = "Sends a test notification",
        description = "Sends a test notification to the specified email address. "
            + "Requires a system user token.",
        security = {
            @SecurityRequirement(name = "AuthorizationToken"),
            @SecurityRequirement(name = "ServiceAuthorization")
        }
    )
    @ApiResponse(responseCode = "200", description = "Notification scheduled successfully")
    @ApiResponse(responseCode = "401", description = "Missing or invalid access token")
    public ResponseEntity<NotificationResponse> notifyTest(
        @RequestBody NotificationTestRequest request
    ) {
        PTCaseEntity ptCase = ptCaseRepository.findByCaseReference(TEST_NOTIFICATION_CASE_REFERENCE).orElseGet(() -> {
            PTCaseEntity newPtCase = PTCaseEntity.builder()
                .caseReference(TEST_NOTIFICATION_CASE_REFERENCE)
                .build();
            return ptCaseRepository.save(newPtCase);
        });

        NotificationRequest notifyRequest = NotificationRequest.builder()
            .template(EmailTemplate.TEST_TEMPLATE)
            .emailAddress(request.getEmailAddress())
            .personalisation(Map.of("testReference", request.getTestReference()))
            .ptCase(ptCase)
            .build();

        NotificationResponse response = notificationService.scheduleEmailNotification(notifyRequest);
        return ResponseEntity.ok(response);
    }

    @Data
    public static class NotificationTestRequest {
        private String emailAddress;
        private String testReference;
    }
}
