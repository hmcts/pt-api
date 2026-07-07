package uk.gov.hmcts.reform.pt.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.pt.model.CreateApplicationResponse;

@RestController
@RequiredArgsConstructor
@Tag(name = "Applications API", description = "CRUD operations for Applications")
public class ApplicationController {

    public ResponseEntity<CreateApplicationResponse> createApplication()
}
