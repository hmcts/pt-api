package uk.gov.hmcts.reform.pt.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.pt.dto.ValidateAccessCodeRequestDto;
import uk.gov.hmcts.reform.pt.idam.IdamAuthenticator;
import uk.gov.hmcts.reform.pt.service.PartyAccessCodeLinkService;

import java.util.UUID;

@RestController
@RequestMapping("/cases")
@RequiredArgsConstructor
public class CasePartyLinkController {

    private final IdamAuthenticator idamAuthenticator;
    private final PartyAccessCodeLinkService linkService;

    @PostMapping("/{caseReference}/validate-access-code")
    public ResponseEntity<Void> validateAccessCode(
        @PathVariable long caseReference,
        @Valid @RequestBody ValidateAccessCodeRequestDto request,
        @RequestHeader("Authorization") String authorization) {

        UUID userId = UUID.fromString(idamAuthenticator.validateAuthToken(authorization).getUserDetails().getUid());
        linkService.linkPartyByAccessCode(caseReference, request.getAccessCode(), userId);
        return ResponseEntity.ok().build();
    }
}
