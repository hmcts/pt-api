package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyRole;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.notify.notification.LandlordAccessCodeNotification;

import static uk.gov.hmcts.reform.pt.ccd.domain.PartyRole.APPLICANT;
import static uk.gov.hmcts.reform.pt.ccd.domain.PartyRole.LANDLORD;
import static uk.gov.hmcts.reform.pt.mapper.ApplicationMapper.getPartyWithRole;

@Service
@RequiredArgsConstructor
public class IssueToRespondentService {

    private final PTCaseService ptCaseService;
    private final RespondentAccessCodeService respondentAccessCodeService;
    private final LandlordAccessCodeNotification landlordAccessCodeNotification;

    @Transactional
    public void issueToRespondent(long caseReference) {
        PTCaseEntity ptCase = ptCaseService.fetchCaseByCaseReference(caseReference)
            .orElseThrow(() -> new CaseNotFoundException(caseReference));

        CasePartyEntity applicant = requireParty(ptCase, APPLICANT);
        CasePartyEntity landlord = requireParty(ptCase, LANDLORD);

        String accessCode = respondentAccessCodeService.createFor(ptCase, landlord);
        landlordAccessCodeNotification.send(ptCase, applicant, landlord, accessCode);
    }

    private CasePartyEntity requireParty(PTCaseEntity ptCase, PartyRole role) {
        CasePartyEntity party = getPartyWithRole(ptCase, role);
        if (party == null) {
            throw new IllegalStateException("Case " + ptCase.getCaseReference() + " has no " + role + " party");
        }
        return party;
    }
}
