package uk.gov.hmcts.reform.pt.testingsupport.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.pt.controllers.advice.ErrorResponse;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.service.PTCaseService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/testing-support")
@ConditionalOnProperty(name = "testing-support.enabled", havingValue = "true")
@Tag(name = "Testing Support")
public class TestingSupportController {

    private final PTCaseService ptCaseService;

    @DeleteMapping("/cases/{caseReference}")
    @Operation(
        summary = "Delete a case",
        description = "Deletes a case and all of its related data. For test data cleanup only.",
        security = {
            @SecurityRequirement(name = "AuthorizationToken"),
            @SecurityRequirement(name = "ServiceAuthorization")
        }
    )
    @ApiResponse(responseCode = "204", description = "Case deleted successfully")
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
}
