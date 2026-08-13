package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicantContactPreferences;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicationType;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.entity.AddressEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.CaseTypeEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.TenancyDetailsEntity;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.repository.CaseApplicationRepository;
import uk.gov.hmcts.reform.pt.repository.AddressRepository;
import uk.gov.hmcts.reform.pt.repository.CasePartyRepository;
import uk.gov.hmcts.reform.pt.repository.PTCaseRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.pt.ccd.domain.TenancyType.ASSURED_PERIODIC_TENANCY;

@ExtendWith(MockitoExtension.class)
class PTCaseServiceTest {

    @Mock
    private PTCaseRepository ptCaseRepository;

    @Mock
    private CasePartyService casePartyService;

    @Mock
    private TenancyDetailsService tenancyDetailsService;

    @Mock
    private CaseTypeService caseTypeService;

    @Mock
    private CaseApplicationRepository caseApplicationRepository;

    @Mock
    private CasePartyRepository casePartyRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ContactPreferencesService contactPreferencesService;

    @Captor
    private ArgumentCaptor<PTCaseEntity> ptCaseEntityCaptor;

    @InjectMocks
    private PTCaseService ptCaseService;

    @Test
    @DisplayName("Should save a case entity built from the case reference")
    void createCase() {
        ApplicationType applicationType = ApplicationType.CHALLENGE_RENT_INCREASE;

        when(casePartyService.createCaseParty(any(), any(), any())).thenReturn(CasePartyEntity.builder().build());
        when(caseTypeService.getCaseTypeOrCreateIfNotExists(applicationType))
            .thenReturn(CaseTypeEntity.builder().build());
        when(tenancyDetailsService.getTenancyDetailsOrCreateIfNotExists(
            eq(ASSURED_PERIODIC_TENANCY),
            any(PTCaseEntity.class))
        ).thenReturn(TenancyDetailsEntity.builder().build());

        PTCase ptCase = PTCase.builder()
            .applicantFirstName("John")
            .applicationType(applicationType)
            .tenancyType(ASSURED_PERIODIC_TENANCY)
            .build();
        UUID userId = UUID.randomUUID();
        long caseReference = 1234567890123456L;

        ptCaseService.createCase(caseReference, userId, ptCase);

        verify(ptCaseRepository).save(ptCaseEntityCaptor.capture());
        PTCaseEntity savedEntity = ptCaseEntityCaptor.getValue();

        assertThat(savedEntity.getCaseReference()).isEqualTo(caseReference);
        verify(casePartyService).createCaseParty(any(PTCaseEntity.class), eq(ptCase), eq(userId));
        verify(caseApplicationRepository).save(any());
    }

    @Test
    @DisplayName("Should update the case party and application when the case exists")
    void updateCaseUpdatesPartyAndApplication() {
        long caseReference = 1234567890123456L;
        ApplicationType applicationType = ApplicationType.CHALLENGE_RENT_INCREASE;

        AddressEntity address = AddressEntity.builder().build();
        CasePartyEntity caseParty = CasePartyEntity.builder()
            .id(1L)
            .addresses(List.of(address))
            .build();

        when(casePartyRepository.findFirstByPtCaseCaseReference(caseReference)).thenReturn(Optional.of(caseParty));

        PTCase ptCase = PTCase.builder()
            .applicantFirstName("Jane")
            .applicantLastName("Doe")
            .email("jane@example.com")
            .postcode("AB1 2CD")
            .applicationType(applicationType)
            .applicantContactPreferences(ApplicantContactPreferences.builder()
                .textUpdates(YesOrNo.YES)
                .textUpdatesPhoneNumber("07777777777")
                .phoneNumberForCalls("01111111111")
                .build())
            .build();

        ptCaseService.updateCase(caseReference, ptCase);

        assertThat(caseParty.getFirstName()).isEqualTo("Jane");
        assertThat(caseParty.getLastName()).isEqualTo("Doe");
        assertThat(caseParty.getEmailAddress()).isEqualTo("jane@example.com");
        assertThat(caseParty.getPhoneNumber()).isEqualTo("01111111111");
        assertThat(caseParty.getMobilePhoneNumber()).isEqualTo("07777777777");
        assertThat(address.getPostcode()).isEqualTo("AB1 2CD");
        verify(casePartyRepository, times(2)).save(caseParty);
        verify(addressRepository).save(address);
        verify(contactPreferencesService).updateContactPreferences(caseParty, ptCase.getApplicantContactPreferences());
    }

    @Test
    @DisplayName("Should update contact preferences and phone numbers")
    void updateContactPreferencesSuccess() {
        CasePartyEntity caseParty = CasePartyEntity.builder().build();
        ApplicantContactPreferences contactPreferences = ApplicantContactPreferences.builder()
            .phoneNumberForCalls("0111")
            .textUpdatesPhoneNumber("0222")
            .build();
        PTCase ptCase = PTCase.builder()
            .applicantContactPreferences(contactPreferences)
            .build();

        ptCaseService.updateContactPreferences(ptCase, caseParty);

        assertThat(caseParty.getPhoneNumber()).isEqualTo("0111");
        assertThat(caseParty.getMobilePhoneNumber()).isEqualTo("0222");
        verify(contactPreferencesService).updateContactPreferences(caseParty, contactPreferences);
        verify(casePartyRepository).save(caseParty);
    }

    @Test
    @DisplayName("Should throw CaseNotFoundException when no case party is found for the case reference")
    void updateCaseThrowsWhenCasePartyNotFound() {
        long caseReference = 1234567890123456L;
        PTCase ptCase = PTCase.builder().build();

        when(casePartyRepository.findFirstByPtCaseCaseReference(caseReference)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ptCaseService.updateCase(caseReference, ptCase))
            .isInstanceOf(CaseNotFoundException.class);
    }
}
