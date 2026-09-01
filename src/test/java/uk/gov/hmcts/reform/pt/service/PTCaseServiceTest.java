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
import uk.gov.hmcts.reform.pt.ccd.domain.CurrentRentDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.HearingPropertyInspectionDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.LandlordDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.MarketRentDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.NoticeOfRentIncreaseDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyRole;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.TenancyAgreementDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.TenantDetails;
import uk.gov.hmcts.reform.pt.entity.AddressEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.CaseTypeEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.TenancyDetailsEntity;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.repository.CaseApplicationRepository;
import uk.gov.hmcts.reform.pt.repository.CasePartyRepository;
import uk.gov.hmcts.reform.pt.repository.PTCaseRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
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
    private ContactPreferencesService contactPreferencesService;

    @Mock
    private PropertyInspectionService propertyInspectionService;

    @Mock
    private NoticeOfRentChangeService noticeOfRentChangeService;

    @Mock
    private DocumentService documentService;

    @Mock
    private MarketRentCaseService marketRentCaseService;

    @Mock
    private AddressService addressService;

    @Captor
    private ArgumentCaptor<PTCaseEntity> ptCaseEntityCaptor;

    @InjectMocks
    private PTCaseService ptCaseService;

    @Test
    @DisplayName("Should save a case entity built from the case reference")
    void createCase() {
        ApplicationType applicationType = ApplicationType.CHALLENGE_RENT_INCREASE;

        when(casePartyService.createApplicantCaseParty(any(), any(), any()))
            .thenReturn(CasePartyEntity.builder().build());
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
        verify(casePartyService).createApplicantCaseParty(any(PTCaseEntity.class), eq(ptCase), eq(userId));
        verify(caseApplicationRepository).save(any());
    }

    @Test
    @DisplayName("Should update the case party and application when the case exists")
    void updateCaseUpdatesPartyAndApplication() {
        long caseReference = 1234567890123456L;
        ApplicationType applicationType = ApplicationType.CHALLENGE_RENT_INCREASE;

        AddressEntity address = AddressEntity.builder().build();
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();
        CasePartyEntity caseParty = CasePartyEntity.builder()
            .id(1L)
            .addresses(List.of(address))
            .ptCase(ptCaseEntity)
            .build();

        when(ptCaseRepository.findByCaseReference(caseReference)).thenReturn(Optional.of(ptCaseEntity));
        when(casePartyService.getPartyForCaseByRole(ptCaseEntity, PartyRole.APPLICANT))
            .thenReturn(Optional.of(caseParty));

        HearingPropertyInspectionDetails hearingInspectionDetails = HearingPropertyInspectionDetails.builder()
            .hearingRequested(YesOrNo.YES)
            .agreeToDecisionWithoutInspection(YesOrNo.NO)
            .noDecisionWithoutInspectionReason("Inspection needed")
            .build();

        NoticeOfRentIncreaseDetails noticeDetails = NoticeOfRentIncreaseDetails.builder()
            .receivedLandlordNoticeProposingNewRent(YesOrNo.YES)
            .noticeLegallyValid(YesOrNo.YES)
            .rentIncreaseToCauseHardship(YesOrNo.NO)
            .build();

        PropertyDetails propertyDetails = PropertyDetails.builder()
            .addressLine1("123 Main St")
            .postcode("AB1 2CD")
            .build();

        CurrentRentDetails currentRentDetails = CurrentRentDetails.builder().build();
        MarketRentDetails marketRentDetails = MarketRentDetails.builder().build();
        TenancyAgreementDetails tenancyAgreementDetails = TenancyAgreementDetails.builder().build();
        LandlordDetails landlordDetails = LandlordDetails.builder().build();

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
            .tenantDetails(TenantDetails.builder()
                .companyName("Test Company")
                .referenceNumberForCommunications("REF12")
                .build())
            .hearingInspectionDetails(hearingInspectionDetails)
            .noticeOfRentIncreaseDetails(noticeDetails)
            .propertyDetails(propertyDetails)
            .currentRentDetails(currentRentDetails)
            .marketRentDetails(marketRentDetails)
            .tenancyAgreementDetails(tenancyAgreementDetails)
            .landlordDetails(landlordDetails)
            .build();

        ptCaseService.updateCase(caseReference, ptCase);

        assertThat(caseParty.getFirstName()).isEqualTo("Jane");
        assertThat(caseParty.getLastName()).isEqualTo("Doe");
        assertThat(caseParty.getEmailAddress()).isEqualTo("jane@example.com");
        assertThat(caseParty.getPhoneNumber()).isEqualTo("01111111111");
        assertThat(caseParty.getMobilePhoneNumber()).isEqualTo("07777777777");
        assertThat(caseParty.getOrganisationName()).isEqualTo("Test Company");
        assertThat(caseParty.getReferenceNumber()).isEqualTo("REF12");
        assertThat(ptCaseEntity.getHearingRequested()).isEqualTo(YesOrNo.YES);
        verify(casePartyRepository, times(3)).save(caseParty);
        verify(addressService).updateAddress(any(PartyDetails.class), eq(caseParty), eq(ptCaseEntity));
        verify(contactPreferencesService).updateContactPreferences(caseParty, ptCase.getApplicantContactPreferences());
        verify(ptCaseRepository).save(ptCaseEntity);
        verify(propertyInspectionService).updatePropertyInspection(ptCaseEntity, hearingInspectionDetails);
        verify(noticeOfRentChangeService).updateNoticeOfRentChangeDetails(noticeDetails, ptCaseEntity);
        verify(documentService).updateDocumentsForNoticeOfRentChange(noticeDetails, ptCaseEntity);
        verify(tenancyDetailsService).updateWithPropertyDetails(ptCaseEntity, propertyDetails);
        verify(marketRentCaseService).updateWithPropertyDetails(ptCaseEntity, propertyDetails);
        verify(documentService).updateDocumentsForPropertyDetails(propertyDetails, ptCaseEntity);
        verify(tenancyDetailsService).updateWithCurrentRentDetails(ptCaseEntity, currentRentDetails);
        verify(marketRentCaseService).updateWithCurrentRentDetails(ptCaseEntity, currentRentDetails);
        verify(marketRentCaseService).updateWithMarketRentDetails(ptCaseEntity, marketRentDetails);
        verify(documentService).updateDocumentsForMarketRentDetails(marketRentDetails, ptCaseEntity);
        verify(tenancyDetailsService).updateWithTenancyAgreementDetails(ptCaseEntity, tenancyAgreementDetails);
        verify(documentService).updateDocumentsForTenancyAgreementDetails(tenancyAgreementDetails, ptCaseEntity);
        verify(casePartyService).updateWithLandlordDetails(ptCaseEntity, landlordDetails);
    }

    @Test
    @DisplayName("Should throw CaseNotFoundException when case is not found for the case reference")
    void updateCaseThrowsWhenPTCaseNotFound() {
        long caseReference = 1234567890123456L;
        PTCase ptCase = PTCase.builder().build();

        when(ptCaseRepository.findByCaseReference(caseReference)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ptCaseService.updateCase(caseReference, ptCase))
            .isInstanceOf(CaseNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw CaseNotFoundException when no applicant case party is found for the case reference")
    void updateCaseThrowsWhenCasePartyNotFound() {
        long caseReference = 1234567890123456L;
        PTCase ptCase = PTCase.builder().build();
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();

        when(ptCaseRepository.findByCaseReference(caseReference)).thenReturn(Optional.of(ptCaseEntity));
        when(casePartyService.getPartyForCaseByRole(ptCaseEntity, PartyRole.APPLICANT)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ptCaseService.updateCase(caseReference, ptCase))
            .isInstanceOf(CaseNotFoundException.class);
    }

    @Test
    @DisplayName("Should update current rent details when currentRentDetails is present")
    void updateCurrentRentDetailsSuccess() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();
        CurrentRentDetails currentRentDetails = CurrentRentDetails.builder().build();
        PTCase ptCase = PTCase.builder()
            .currentRentDetails(currentRentDetails)
            .build();

        ptCaseService.updateCurrentRentDetails(ptCase, ptCaseEntity);

        verify(tenancyDetailsService).updateWithCurrentRentDetails(ptCaseEntity, currentRentDetails);
        verify(marketRentCaseService).updateWithCurrentRentDetails(ptCaseEntity, currentRentDetails);
    }

    @Test
    @DisplayName("Should skip updating current rent details when currentRentDetails is null")
    void updateCurrentRentDetailsSkippedWhenNull() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();
        PTCase ptCase = PTCase.builder()
            .currentRentDetails(null)
            .build();

        ptCaseService.updateCurrentRentDetails(ptCase, ptCaseEntity);

        verify(tenancyDetailsService, never()).updateWithCurrentRentDetails(any(), any());
        verify(marketRentCaseService, never()).updateWithCurrentRentDetails(any(), any());
    }

    @Test
    @DisplayName("Should update market rent details when marketRentDetails is present")
    void updateMarketRentDetailsSuccess() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();
        MarketRentDetails marketRentDetails = MarketRentDetails.builder().build();
        PTCase ptCase = PTCase.builder()
            .marketRentDetails(marketRentDetails)
            .build();

        ptCaseService.updateMarketRentDetails(ptCase, ptCaseEntity);

        verify(marketRentCaseService).updateWithMarketRentDetails(ptCaseEntity, marketRentDetails);
        verify(documentService).updateDocumentsForMarketRentDetails(marketRentDetails, ptCaseEntity);
    }

    @Test
    @DisplayName("Should skip updating market rent details when marketRentDetails is null")
    void updateMarketRentDetailsSkippedWhenNull() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();
        PTCase ptCase = PTCase.builder()
            .marketRentDetails(null)
            .build();

        ptCaseService.updateMarketRentDetails(ptCase, ptCaseEntity);

        verify(marketRentCaseService, never()).updateWithMarketRentDetails(any(), any());
        verify(documentService, never()).updateDocumentsForMarketRentDetails(any(), any());
    }

    @Test
    @DisplayName("Should update tenancy agreement details when tenancyAgreementDetails is present")
    void updateTenancyAgreementDetailsSuccess() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();
        TenancyAgreementDetails tenancyAgreementDetails = TenancyAgreementDetails.builder().build();
        PTCase ptCase = PTCase.builder()
            .tenancyAgreementDetails(tenancyAgreementDetails)
            .build();

        ptCaseService.updateTenancyAgreementDetails(ptCase, ptCaseEntity);

        verify(tenancyDetailsService).updateWithTenancyAgreementDetails(ptCaseEntity, tenancyAgreementDetails);
        verify(documentService).updateDocumentsForTenancyAgreementDetails(tenancyAgreementDetails, ptCaseEntity);
    }

    @Test
    @DisplayName("Should skip updating tenancy agreement details when tenancyAgreementDetails is null")
    void updateTenancyAgreementDetailsSkippedWhenNull() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();
        PTCase ptCase = PTCase.builder()
            .tenancyAgreementDetails(null)
            .build();

        ptCaseService.updateTenancyAgreementDetails(ptCase, ptCaseEntity);

        verify(tenancyDetailsService, never()).updateWithTenancyAgreementDetails(any(), any());
        verify(documentService, never()).updateDocumentsForTenancyAgreementDetails(any(), any());
    }

    @Test
    @DisplayName("Should update landlord details when landlordDetails is present")
    void updateLandlordDetailsSuccess() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();
        LandlordDetails landlordDetails = LandlordDetails.builder().build();
        PTCase ptCase = PTCase.builder()
            .landlordDetails(landlordDetails)
            .build();

        ptCaseService.updateLandlordDetails(ptCase, ptCaseEntity);

        verify(casePartyService).updateWithLandlordDetails(ptCaseEntity, landlordDetails);
    }

    @Test
    @DisplayName("Should skip updating landlord details when landlordDetails is null")
    void updateLandlordDetailsSkippedWhenNull() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();
        PTCase ptCase = PTCase.builder()
            .landlordDetails(null)
            .build();

        ptCaseService.updateLandlordDetails(ptCase, ptCaseEntity);

        verify(casePartyService, never()).updateWithLandlordDetails(any(), any());
    }

    @Test
    @DisplayName("Should update tenant details and save")
    void updateTenantDetailsSuccess() {
        CasePartyEntity caseParty = CasePartyEntity.builder().build();
        TenantDetails tenantDetails = TenantDetails.builder()
            .companyName("ACME")
            .referenceNumberForCommunications("12345")
            .build();
        PTCase ptCase = PTCase.builder()
            .tenantDetails(tenantDetails)
            .build();

        ptCaseService.updateTenantDetails(ptCase, caseParty);

        assertThat(caseParty.getOrganisationName()).isEqualTo("ACME");
        assertThat(caseParty.getReferenceNumber()).isEqualTo("12345");
        verify(casePartyRepository).save(caseParty);
    }

    @Test
    @DisplayName("Should update hearing and property inspection details")
    void updateHearingOrPropertyInspectionDetailsSuccess() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();
        HearingPropertyInspectionDetails hearingInspectionDetails = HearingPropertyInspectionDetails.builder()
            .hearingRequested(YesOrNo.YES)
            .agreeToDecisionWithoutInspection(YesOrNo.NO)
            .noDecisionWithoutInspectionReason("Inspection needed")
            .build();
        PTCase ptCase = PTCase.builder()
            .hearingInspectionDetails(hearingInspectionDetails)
            .build();

        ptCaseService.updateHearingOrPropertyInspectionDetails(ptCase, ptCaseEntity);

        assertThat(ptCaseEntity.getHearingRequested()).isEqualTo(YesOrNo.YES);
        verify(ptCaseRepository).save(ptCaseEntity);
        verify(propertyInspectionService).updatePropertyInspection(ptCaseEntity, hearingInspectionDetails);
    }

    @Test
    @DisplayName("Should update notice of rent change details and documents when details present")
    void updateNoticeOfRentChangeDetailsSuccess() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();
        NoticeOfRentIncreaseDetails noticeDetails = NoticeOfRentIncreaseDetails.builder()
            .receivedLandlordNoticeProposingNewRent(YesOrNo.YES)
            .noticeLegallyValid(YesOrNo.NO)
            .rentIncreaseToCauseHardship(YesOrNo.YES)
            .build();
        PTCase ptCase = PTCase.builder()
            .noticeOfRentIncreaseDetails(noticeDetails)
            .build();

        ptCaseService.updateNoticeOfRentChangeDetails(ptCase, ptCaseEntity);

        verify(noticeOfRentChangeService).updateNoticeOfRentChangeDetails(noticeDetails, ptCaseEntity);
        verify(documentService).updateDocumentsForNoticeOfRentChange(noticeDetails, ptCaseEntity);
    }

    @Test
    @DisplayName("Should update contact preferences and phone numbers on case party")
    void updateContactPreferencesSuccess() {
        CasePartyEntity caseParty = CasePartyEntity.builder().build();
        ApplicantContactPreferences preferences = ApplicantContactPreferences.builder()
            .phoneNumberForCalls("01234567890")
            .textUpdatesPhoneNumber("07123456789")
            .build();
        PTCase ptCase = PTCase.builder()
            .applicantContactPreferences(preferences)
            .build();

        ptCaseService.updateContactPreferences(ptCase, caseParty);

        verify(contactPreferencesService).updateContactPreferences(caseParty, preferences);
        assertThat(caseParty.getPhoneNumber()).isEqualTo("01234567890");
        assertThat(caseParty.getMobilePhoneNumber()).isEqualTo("07123456789");
        verify(casePartyRepository).save(caseParty);
    }

    @Test
    @DisplayName("Should skip updating contact preferences when applicantContactPreferences is null")
    void updateContactPreferencesSkippedWhenNull() {
        CasePartyEntity caseParty = CasePartyEntity.builder().build();
        PTCase ptCase = PTCase.builder()
            .applicantContactPreferences(null)
            .build();

        ptCaseService.updateContactPreferences(ptCase, caseParty);

        verify(contactPreferencesService, never()).updateContactPreferences(any(), any());
        verify(casePartyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should skip updating tenant details when tenantDetails is null")
    void updateTenantDetailsSkippedWhenNull() {
        CasePartyEntity caseParty = CasePartyEntity.builder().build();
        PTCase ptCase = PTCase.builder()
            .tenantDetails(null)
            .build();

        ptCaseService.updateTenantDetails(ptCase, caseParty);

        verify(casePartyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should skip updating hearing or property inspection details when hearingInspectionDetails is null")
    void updateHearingOrPropertyInspectionDetailsSkippedWhenNull() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();
        PTCase ptCase = PTCase.builder()
            .hearingInspectionDetails(null)
            .build();

        ptCaseService.updateHearingOrPropertyInspectionDetails(ptCase, ptCaseEntity);

        verify(ptCaseRepository, never()).save(any());
        verify(propertyInspectionService, never()).updatePropertyInspection(any(), any());
    }

    @Test
    @DisplayName("Should update property details when propertyDetails is present")
    void updatePropertyDetailsSuccess() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();
        CasePartyEntity caseParty = CasePartyEntity.builder().build();
        PropertyDetails propertyDetails = PropertyDetails.builder()
            .addressLine1("10 High Street")
            .addressLine2("Flat 2")
            .postTown("London")
            .county("Greater London")
            .postcode("SW1A 1AA")
            .build();
        PTCase ptCase = PTCase.builder()
            .propertyDetails(propertyDetails)
            .build();

        ptCaseService.updatePropertyDetails(ptCase, ptCaseEntity, caseParty);

        ArgumentCaptor<PartyDetails> partyDetailsCaptor = ArgumentCaptor.forClass(PartyDetails.class);
        verify(addressService).updateAddress(partyDetailsCaptor.capture(), eq(caseParty), eq(ptCaseEntity));
        PartyDetails captured = partyDetailsCaptor.getValue();
        assertThat(captured.getAddressLine1()).isEqualTo("10 High Street");
        assertThat(captured.getAddressLine2()).isEqualTo("Flat 2");
        assertThat(captured.getPostTown()).isEqualTo("London");
        assertThat(captured.getCounty()).isEqualTo("Greater London");
        assertThat(captured.getPostcode()).isEqualTo("SW1A 1AA");

        verify(tenancyDetailsService).updateWithPropertyDetails(ptCaseEntity, propertyDetails);
        verify(marketRentCaseService).updateWithPropertyDetails(ptCaseEntity, propertyDetails);
        verify(documentService).updateDocumentsForPropertyDetails(propertyDetails, ptCaseEntity);
    }

    @Test
    @DisplayName("Should skip updating property details when propertyDetails is null")
    void updatePropertyDetailsSkippedWhenNull() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();
        CasePartyEntity caseParty = CasePartyEntity.builder().build();
        PTCase ptCase = PTCase.builder()
            .propertyDetails(null)
            .build();

        ptCaseService.updatePropertyDetails(ptCase, ptCaseEntity, caseParty);

        verify(addressService, never()).updateAddress(any(), any(), any());
        verify(tenancyDetailsService, never()).updateWithPropertyDetails(any(), any());
        verify(marketRentCaseService, never()).updateWithPropertyDetails(any(), any());
        verify(documentService, never()).updateDocumentsForPropertyDetails(any(), any());
    }

    @Test
    @DisplayName("Should skip updating notice of rent change details when noticeOfRentIncreaseDetails is null")
    void updateNoticeOfRentChangeDetailsSkippedWhenNull() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();
        PTCase ptCase = PTCase.builder()
            .noticeOfRentIncreaseDetails(null)
            .build();

        ptCaseService.updateNoticeOfRentChangeDetails(ptCase, ptCaseEntity);

        verify(noticeOfRentChangeService, never()).updateNoticeOfRentChangeDetails(any(), any());
        verify(documentService, never()).updateDocumentsForNoticeOfRentChange(any(), any());
    }
}
