package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyRole;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.PartyAccessCodeEntity;
import uk.gov.hmcts.reform.pt.exception.AccessCodeAlreadyUsedException;
import uk.gov.hmcts.reform.pt.exception.AccessCodeExpiredException;
import uk.gov.hmcts.reform.pt.exception.InvalidAccessCodeException;
import uk.gov.hmcts.reform.pt.repository.PartyAccessCodeRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PartyAccessCodeLinkService {

    private final PartyAccessCodeRepository repository;
    private final PasswordEncoder encoder;
    private final CaseRoleAssignmentService caseRoleAssignmentService;
    private final Clock clock;

    @Transactional
    public void linkPartyByAccessCode(long caseReference, String enteredCode, UUID idamUserId) {
        PartyAccessCodeEntity accessCode = repository.findAllByPtCaseCaseReference(caseReference)
            .stream()
            .filter(pac -> encoder.matches(enteredCode, pac.getCode()))
            .findFirst()
            .orElseThrow(() -> new InvalidAccessCodeException("Invalid access code for case " + caseReference));

        if (accessCode.isExpired(LocalDateTime.now(clock))) {
            throw new AccessCodeExpiredException("Access code has expired");
        }

        CasePartyEntity party = getParty(accessCode);

        boolean linkedElsewhere = party
            .getPtCase()
            .getParties()
            .stream()
            .anyMatch(otherParty ->
                          !otherParty.getId().equals(party.getId()) && idamUserId.equals(otherParty.getIdamId()));
        if (linkedElsewhere) {
            throw new AccessCodeAlreadyUsedException("User is already linked to another party on this case");
        }

        party.setIdamId(idamUserId);
        accessCode.setUsedAt(LocalDateTime.now(clock));
        caseRoleAssignmentService.assignCaseRole(caseReference, idamUserId, UserRole.DEFENDANT);
    }

    private static CasePartyEntity getParty(PartyAccessCodeEntity accessCode) {
        CasePartyEntity party = accessCode.getParty();

        // TODO: this check may need to be altered in future as
        //  LANDLORD_REPRESENTATIVE and LETTING_AGENT will be allowed to respond on the landlords behalf
        if (party.getCasePartyRole() != PartyRole.LANDLORD) {
            throw new InvalidAccessCodeException("Access code can only be used by the landlord on this case");
        }

        if (party.getIdamId() != null) {
            // The party is already linked to an IDAM user signifying the code has been used
            throw new AccessCodeAlreadyUsedException("This access code has already been used");
        }
        return party;
    }
}
