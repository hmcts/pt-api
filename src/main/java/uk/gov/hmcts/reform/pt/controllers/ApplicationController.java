package uk.gov.hmcts.reform.pt.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.pt.dto.ApplicationDto;
import uk.gov.hmcts.reform.pt.dto.EnrichedApplicationDto;
import uk.gov.hmcts.reform.pt.idam.UserInfo;
import uk.gov.hmcts.reform.pt.security.SecurityContextService;
import uk.gov.hmcts.reform.pt.service.CaseApplicationService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/applications")
public class ApplicationController {

    private final CaseApplicationService caseApplicationService;
    private final SecurityContextService securityContextService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Get all the cases related to authenticated citizen",
        description = "Returns all cases where the authenticated user is linked",
        security = {
            @SecurityRequirement(name = "AuthorizationToken"),
            @SecurityRequirement(name = "ServiceAuthorization")
        }
    )
    @ApiResponse(responseCode = "200", description = "Cases retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Invalid access token")
    @ApiResponse(responseCode = "403", description = "Invalid Service Authorization")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @ApiResponse(responseCode = "503", description = "Service temporarily unavailable")
    public ResponseEntity<List<ApplicationDto>> getCasesForUser(
        @Parameter(description = "Bearer token for user authentication", required = true)
        @RequestHeader("Authorization") String authorization,
        @Parameter(description = "Service-to-Service (S2S) authorization token", required = true)
        @RequestHeader("ServiceAuthorization") String s2sToken
    ) {
        UserInfo userInfo = securityContextService.getCurrentUserDetails();

        return ResponseEntity.ok(caseApplicationService.getCasesForUser(UUID.fromString(userInfo.getUid())));
    }

    @GetMapping(value = "/{caseReference}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Get case for the case reference",
        description = "Returns case for the case reference",
        security = {
            @SecurityRequirement(name = "AuthorizationToken"),
            @SecurityRequirement(name = "ServiceAuthorization")
        }
    )
    @ApiResponse(responseCode = "200", description = "Case retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid case reference")
    @ApiResponse(responseCode = "401", description = "Invalid access token")
    @ApiResponse(responseCode = "403", description = "Invalid Service Authorization")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @ApiResponse(responseCode = "503", description = "Service temporarily unavailable")
    public ResponseEntity<EnrichedApplicationDto> getCaseByCaseReference(
        @Parameter(description = "Bearer token for user authentication", required = true)
        @RequestHeader("Authorization") String authorization,
        @Parameter(description = "Service-to-Service (S2S) authorization token", required = true)
        @RequestHeader("ServiceAuthorization") String s2sToken,
        @PathVariable Long caseReference
    ) {
        UserInfo userInfo = securityContextService.getCurrentUserDetails();

        return ResponseEntity.ok(
            caseApplicationService.getCaseByCaseReference(caseReference, UUID.fromString(userInfo.getUid())));
    }
}
