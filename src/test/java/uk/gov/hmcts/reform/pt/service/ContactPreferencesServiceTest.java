package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicantContactPreferences;
import uk.gov.hmcts.reform.pt.entity.CasePartyContactPreferenceEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.repository.CasePartyContactPreferenceRepository;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ContactPreferencesServiceTest {

    @Mock
    private CasePartyContactPreferenceRepository casePartyContactPreferenceRepository;

    @InjectMocks
    private ContactPreferencesService contactPreferencesService;

    @Test
    @DisplayName("Should create new contact preferences when none exist")
    void updateContactPreferencesCreatesNew() {
        CasePartyEntity caseParty = CasePartyEntity.builder()
            .contactPreferences(Collections.emptyList())
            .build();
        ApplicantContactPreferences contactPreferenceData = ApplicantContactPreferences.builder()
            .textUpdates(YesOrNo.YES)
            .phoneNumberForCalls("0123456789")
            .build();

        contactPreferencesService.updateContactPreferences(caseParty, contactPreferenceData);

        ArgumentCaptor<CasePartyContactPreferenceEntity> captor =
            ArgumentCaptor.forClass(CasePartyContactPreferenceEntity.class);
        verify(casePartyContactPreferenceRepository).save(captor.capture());
        CasePartyContactPreferenceEntity saved = captor.getValue();

        assertThat(saved.getParty()).isEqualTo(caseParty);
        assertThat(saved.getContactByText()).isEqualTo(YesOrNo.YES);
    }

    @Test
    @DisplayName("Should update existing contact preferences when they exist")
    void updateContactPreferencesUpdatesExisting() {
        CasePartyContactPreferenceEntity existing = CasePartyContactPreferenceEntity.builder()
            .contactByText(YesOrNo.NO)
            .build();
        CasePartyEntity caseParty = CasePartyEntity.builder()
            .contactPreferences(List.of(existing))
            .build();
        ApplicantContactPreferences contactPreferenceData = ApplicantContactPreferences.builder()
            .textUpdates(YesOrNo.YES)
            .phoneNumberForCalls(null)
            .build();

        contactPreferencesService.updateContactPreferences(caseParty, contactPreferenceData);

        verify(casePartyContactPreferenceRepository).save(existing);
        assertThat(existing.getContactByText()).isEqualTo(YesOrNo.YES);
        assertThat(existing.getParty()).isEqualTo(caseParty);
    }
}
