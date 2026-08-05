package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicantContactPreferences;
import uk.gov.hmcts.reform.pt.entity.CasePartyContactPreferenceEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.repository.CasePartyContactPreferenceRepository;

@Service
@RequiredArgsConstructor
public class ContactPreferencesService {

    private final CasePartyContactPreferenceRepository casePartyContactPreferenceRepository;

    @Transactional
    public void updateContactPreferences(CasePartyEntity caseParty, ApplicantContactPreferences contactPreferenceData) {
        CasePartyContactPreferenceEntity contactPreferences = caseParty.getContactPreferences().stream()
            .findFirst()
            .orElse(new CasePartyContactPreferenceEntity());

        contactPreferences.setParty(caseParty);
        contactPreferences.setContactByText(contactPreferenceData.getTextUpdates());
        contactPreferences.setContactByPhone(
            contactPreferenceData.getPhoneNumberForCalls() != null
                ? YesOrNo.YES
                : YesOrNo.NO
        );
        casePartyContactPreferenceRepository.save(contactPreferences);
    }
}
