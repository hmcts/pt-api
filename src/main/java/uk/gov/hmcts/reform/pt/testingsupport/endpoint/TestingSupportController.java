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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.AccessProfile;
import uk.gov.hmcts.reform.pt.controllers.advice.ErrorResponse;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.idam.IdamAuthenticator;
import uk.gov.hmcts.reform.pt.idam.UserInfo;
import uk.gov.hmcts.reform.pt.service.PTCaseService;

import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/testing-support")
@ConditionalOnProperty(name = "testing-support.enabled", havingValue = "true")
@Tag(name = "Testing Support")
public class TestingSupportController {

    private static final String SYSTEM_USER_ROLE = AccessProfile.SYSTEM_USER.getRole();

    private final PTCaseService ptCaseService;
    private final IdamAuthenticator idamAuthenticator;

    @DeleteMapping("/cases/{caseReference}")
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
        @Parameter(description = "Bearer token for user authentication", required = true)
        @RequestHeader(AUTHORIZATION) String authorization,
        @Parameter(description = "The case reference to delete", required = true)
        @PathVariable long caseReference
    ) {
        UserInfo user = idamAuthenticator.validateAuthToken(authorization).getUserDetails();
        List<String> roles = user.getRoles() == null ? List.of() : user.getRoles();

        if (!roles.contains(SYSTEM_USER_ROLE)) {
            log.warn("Rejected testing-support delete of case {}: caller lacks the {} role",
                caseReference, SYSTEM_USER_ROLE);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("Requires the " + SYSTEM_USER_ROLE + " role"));
        }

        try {
            ptCaseService.deleteCase(caseReference);
        } catch (CaseNotFoundException e) {
            log.error("Case not found", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }

        return ResponseEntity.noContent().build();
    }
}
