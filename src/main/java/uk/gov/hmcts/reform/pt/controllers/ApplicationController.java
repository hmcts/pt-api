package uk.gov.hmcts.reform.pt.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.reform.pt.model.CreateApplicationRequest;
import uk.gov.hmcts.reform.pt.model.CreateApplicationResponse;
import uk.gov.hmcts.reform.pt.service.PTCaseService;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
@Tag(name = "Applications API", description = "CRUD operations for Applications")
public class ApplicationController {

    private final PTCaseService ptCaseService;

    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponse(responseCode = "201", description = "Successfully created application", content = @Content())
    @ApiResponse(responseCode = "400", description = "Invalid access code for this case", content = @Content())
    @ApiResponse(responseCode = "401", description = "Invalid access token", content = @Content())
    @ApiResponse(responseCode = "403", description = "Invalid service authorization", content = @Content())
    @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content())
    public ResponseEntity<CreateApplicationResponse> createApplication(
        @Parameter(description = "Service-to-Service (S2S) authorization token", required = true)
        @RequestHeader("ServiceAuthorization") String s2sToken,
        @Valid @RequestBody CreateApplicationRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ptCaseService.createCase(request));
    }
}
