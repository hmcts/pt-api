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
import uk.gov.hmcts.reform.pt.ccd.domain.HearingPropertyInspectionDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.NoticeOfRentIncreaseDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.TenantDetails;
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

import java.util.ArrayList;
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
    private AddressRepository addressRepository;

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
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();
        CasePartyEntity caseParty = CasePartyEntity.builder()
            .id(1L)
            .addresses(List.of(address))
            .ptCase(ptCaseEntity)
            .build();

        when(casePartyRepository.findFirstByPtCaseCaseReference(caseReference)).thenReturn(Optional.of(caseParty));

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
            .build();

        ptCaseService.updateCase(caseReference, ptCase);

        assertThat(caseParty.getFirstName()).isEqualTo("Jane");
        assertThat(caseParty.getLastName()).isEqualTo("Doe");
        assertThat(caseParty.getEmailAddress()).isEqualTo("jane@example.com");
        assertThat(caseParty.getPhoneNumber()).isEqualTo("01111111111");
        assertThat(caseParty.getMobilePhoneNumber()).isEqualTo("07777777777");
        assertThat(caseParty.getOrganisationName()).isEqualTo("Test Company");
        assertThat(caseParty.getReferenceNumber()).isEqualTo("REF12");
        assertThat(address.getAddressLine1()).isEqualTo("123 Main St");
        assertThat(address.getPostcode()).isEqualTo("AB1 2CD");
        assertThat(ptCaseEntity.getHearingRequested()).isEqualTo(YesOrNo.YES);
        verify(casePartyRepository, times(3)).save(caseParty);
        verify(addressRepository).save(address);
        verify(contactPreferencesService).updateContactPreferences(caseParty, ptCase.getApplicantContactPreferences());
        verify(ptCaseRepository).save(ptCaseEntity);
        verify(propertyInspectionService).updatePropertyInspection(ptCaseEntity, hearingInspectionDetails);
        verify(noticeOfRentChangeService).updateNoticeOfRentChangeDetails(noticeDetails, ptCaseEntity);
        verify(documentService).updateDocumentsForNoticeOfRentChange(noticeDetails, ptCaseEntity);
        verify(tenancyDetailsService).updateWithPropertyDetails(ptCaseEntity, propertyDetails);
        verify(marketRentCaseService).updateWithPropertyDetails(ptCaseEntity, propertyDetails);
        verify(documentService).updateDocumentsForPropertyDetails(propertyDetails, ptCaseEntity);
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

    @Test
    @DisplayName("Should update property details when propertyDetails is present and party has existing address")
    void updatePropertyDetailsSuccess() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();
        AddressEntity existingAddress = AddressEntity.builder().build();
        CasePartyEntity caseParty = CasePartyEntity.builder()
            .addresses(List.of(existingAddress))
            .build();

        PropertyDetails propertyDetails = PropertyDetails.builder()
            .addressLine1("1 High Street")
            .addressLine2("Suite 2")
            .postTown("London")
            .county("Greater London")
            .postcode("SW1A 1AA")
            .build();

        PTCase ptCase = PTCase.builder()
            .propertyDetails(propertyDetails)
            .build();

        ptCaseService.updatePropertyDetails(ptCase, ptCaseEntity, caseParty);

        assertThat(existingAddress.getAddressLine1()).isEqualTo("1 High Street");
        assertThat(existingAddress.getAddressLine2()).isEqualTo("Suite 2");
        assertThat(existingAddress.getPostTown()).isEqualTo("London");
        assertThat(existingAddress.getCounty()).isEqualTo("Greater London");
        assertThat(existingAddress.getPostcode()).isEqualTo("SW1A 1AA");
        assertThat(existingAddress.getParty()).isEqualTo(caseParty);
        assertThat(existingAddress.getPtCase()).isEqualTo(ptCaseEntity);

        verify(addressRepository).save(existingAddress);
        verify(tenancyDetailsService).updateWithPropertyDetails(ptCaseEntity, propertyDetails);
        verify(marketRentCaseService).updateWithPropertyDetails(ptCaseEntity, propertyDetails);
        verify(documentService).updateDocumentsForPropertyDetails(propertyDetails, ptCaseEntity);
    }

    @Test
    @DisplayName("Should update property details when party addresses list is empty")
    void updatePropertyDetailsWhenAddressListEmpty() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();
        CasePartyEntity caseParty = CasePartyEntity.builder()
            .addresses(new ArrayList<>())
            .build();

        PropertyDetails propertyDetails = PropertyDetails.builder()
            .addressLine1("1 High Street")
            .postcode("SW1A 1AA")
            .build();

        PTCase ptCase = PTCase.builder()
            .propertyDetails(propertyDetails)
            .build();

        ptCaseService.updatePropertyDetails(ptCase, ptCaseEntity, caseParty);

        ArgumentCaptor<AddressEntity> addressCaptor = ArgumentCaptor.forClass(AddressEntity.class);
        verify(addressRepository).save(addressCaptor.capture());
        AddressEntity savedAddress = addressCaptor.getValue();

        assertThat(savedAddress.getAddressLine1()).isEqualTo("1 High Street");
        assertThat(savedAddress.getPostcode()).isEqualTo("SW1A 1AA");
        assertThat(savedAddress.getParty()).isEqualTo(caseParty);
        assertThat(savedAddress.getPtCase()).isEqualTo(ptCaseEntity);

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

        verify(addressRepository, never()).save(any());
        verify(tenancyDetailsService, never()).updateWithPropertyDetails(any(), any());
        verify(marketRentCaseService, never()).updateWithPropertyDetails(any(), any());
        verify(documentService, never()).updateDocumentsForPropertyDetails(any(), any());
    }
}
