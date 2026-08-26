package uk.gov.hmcts.reform.pt.mapper;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicationType;
import uk.gov.hmcts.reform.pt.ccd.domain.DocumentType;
import uk.gov.hmcts.reform.pt.ccd.domain.Frequency;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyType;
import uk.gov.hmcts.reform.pt.ccd.domain.TenancyType;
import uk.gov.hmcts.reform.pt.ccd.domain.YesNoNotSure;
import uk.gov.hmcts.reform.pt.dto.ApplicationDto;
import uk.gov.hmcts.reform.pt.dto.ContactPreferencesDto;
import uk.gov.hmcts.reform.pt.dto.CurrentRentsDetailsDto;
import uk.gov.hmcts.reform.pt.dto.DocumentDto;
import uk.gov.hmcts.reform.pt.dto.HearingInspectionDetailsDto;
import uk.gov.hmcts.reform.pt.dto.MarketRentDto;
import uk.gov.hmcts.reform.pt.dto.NoticeOfRentIncreaseDto;
import uk.gov.hmcts.reform.pt.dto.PropertyDetailsDto;
import uk.gov.hmcts.reform.pt.dto.TenancyAgreementDto;
import uk.gov.hmcts.reform.pt.dto.TenantDetailsDto;
import uk.gov.hmcts.reform.pt.entity.AddressEntity;
import uk.gov.hmcts.reform.pt.entity.CaseApplicationEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyAccessEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyContactPreferenceEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.CaseTypeEntity;
import uk.gov.hmcts.reform.pt.entity.DocumentEntity;
import uk.gov.hmcts.reform.pt.entity.MarketRentCaseEntity;
import uk.gov.hmcts.reform.pt.entity.NoticeOfRentChangeEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.PropertyInspectionEntity;
import uk.gov.hmcts.reform.pt.entity.TenancyDetailsEntity;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.exception.CasePartyNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ApplicationMapperTest {

    private static final long CASE_REFERENCE = 1234567890123456L;
    private static final ApplicationType APPLICATION_TYPE = ApplicationType.CHALLENGE_RENT_INCREASE;
    private static final TenancyType TENANCY_TYPE = TenancyType.ASSURED_PERIODIC_TENANCY;
    private static final String POSTCODE = "AB12 3CD";
    private static final String FIRST_NAME = "FirstName";
    private static final String LAST_NAME = "LastName";
    private static final String EMAIL = "test@test.com";
    private static final String PHONE_NUMBER = "0123456789";
    private static final String MOBILE_NUMBER = "0712345678";
    private static final String COMPANY_NAME = "Test Company Ltd";
    private static final String REFERENCE_NUMBER = "REF12";
    private static final LocalDateTime CREATED_DATE = LocalDateTime.of(2026, 2, 24, 9, 0);

    @Test
    public void shouldMapToDtoFromEntity() {
        UUID userId = UUID.randomUUID();
        CaseApplicationEntity entity = fullEntity(userId);

        ApplicationDto result = ApplicationMapper.toDto(entity);

        assertThat(result.getCaseReference()).isEqualTo(CASE_REFERENCE);
        assertThat(result.getPostcode()).isEqualTo(POSTCODE);
        assertThat(result.getApplicationType()).isEqualTo(APPLICATION_TYPE);
        assertThat(result.getTenancyType()).isEqualTo(TENANCY_TYPE);
        assertThat(result.getApplicantFirstName()).isEqualTo(FIRST_NAME);
        assertThat(result.getApplicantLastName()).isEqualTo(LAST_NAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getApplicantIdamUserId()).isEqualTo(userId);
        assertThat(result.getCreatedDate()).isEqualTo(CREATED_DATE);

        ContactPreferencesDto contactPreferences = result.getApplicantContactPreferences();
        assertThat(contactPreferences.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
        assertThat(contactPreferences.getMobilePhoneNumber()).isEqualTo(MOBILE_NUMBER);
        assertThat(contactPreferences.getContactByText()).isEqualTo(YesOrNo.YES);

        TenantDetailsDto tenantDetails = result.getTenantDetails();
        assertThat(tenantDetails.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(tenantDetails.getLastName()).isEqualTo(LAST_NAME);
        assertThat(tenantDetails.getCompanyName()).isEqualTo(COMPANY_NAME);
        assertThat(tenantDetails.getReferenceNumberForCommunications()).isEqualTo(REFERENCE_NUMBER);

        HearingInspectionDetailsDto hearingInspectionDetails = result.getHearingInspectionDetails();
        assertThat(hearingInspectionDetails.getHearingRequested()).isEqualTo(YesOrNo.YES);
        assertThat(hearingInspectionDetails.getAgreeToDecisionWithoutInspection()).isEqualTo(YesOrNo.YES);
        assertThat(hearingInspectionDetails.getNoDecisionWithoutInspectionReason()).isEqualTo("Inspection reason");

        NoticeOfRentIncreaseDto noticeOfRentIncreaseDetails = result.getNoticeOfRentIncreaseDetails();
        assertThat(noticeOfRentIncreaseDetails).isNotNull();

        PropertyDetailsDto propertyDetails = result.getPropertyDetails();
        assertThat(propertyDetails).isNotNull();
        assertThat(propertyDetails.getAddressLine1()).isEqualTo("123 Test St");
        assertThat(propertyDetails.getPostTown()).isEqualTo("London");
        assertThat(propertyDetails.getPropertyType()).isEqualTo(PropertyType.TERRACED_HOUSE);

        CurrentRentsDetailsDto currentRentsDetails = result.getCurrentRentsDetails();
        assertThat(currentRentsDetails).isNotNull();

        MarketRentDto marketRentDetails = result.getMarketRentDetails();
        assertThat(marketRentDetails).isNotNull();
        assertThat(marketRentDetails.getApplicantSuggestedMonthlyMarketRent()).isEqualTo(new BigDecimal("1200.00"));
        assertThat(marketRentDetails.getApplicantSuggestedMonthlyMarketRentReasons())
            .isEqualTo("Market rate for the area");
        assertThat(marketRentDetails.getAdditionalPropertyInfoToConsiderWhenDetermining()).isEqualTo(YesOrNo.YES);
        assertThat(marketRentDetails.getAdditionalPropertyInfoToConsiderWhenDeterminingDetails())
            .isEqualTo("Recently refurbished");

        TenancyAgreementDto tenancyAgreementDetails = result.getTenancyAgreementDetails();
        assertThat(tenancyAgreementDetails).isNotNull();
        assertThat(tenancyAgreementDetails.getCopyOfTenancyAgreement()).isEqualTo(YesOrNo.YES);
        assertThat(tenancyAgreementDetails.getNoTenancyAgreementReason()).isEqualTo("No agreement reason");
        assertThat(tenancyAgreementDetails.getTenancyAgreementEvidence()).isNotNull();
        assertThat(tenancyAgreementDetails.getTenancyAgreementEvidence().getUrl())
            .isEqualTo("http://dm-store/doc/tenancy-agreement");
    }

    @Test
    public void shouldThrowCasePartyNotFoundExceptionWhenCasePartyIsNull() {
        CaseApplicationEntity entity = CaseApplicationEntity.builder()
            .caseParty(null)
            .build();

        assertThatThrownBy(() -> ApplicationMapper.toDto(entity))
            .isInstanceOf(CasePartyNotFoundException.class)
            .hasMessageContaining("Case party not found for application");
    }

    @Test
    public void shouldThrowCaseNotFoundExceptionWhenPtCaseIsNull() {
        CasePartyEntity caseParty = caseParty(
            null,
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList()
        );
        CaseApplicationEntity entity = CaseApplicationEntity.builder()
            .caseParty(caseParty)
            .build();

        assertThatThrownBy(() -> ApplicationMapper.toDto(entity))
            .isInstanceOf(CaseNotFoundException.class)
            .hasMessageContaining("Case not found for application");
    }

    @Test
    public void shouldMapContactPreferences() {
        CasePartyEntity caseParty = caseParty(
            null,
            Collections.emptyList(),
            contactPreferences(YesOrNo.YES, YesOrNo.NO),
            Collections.emptyList()
        );

        ContactPreferencesDto result = ApplicationMapper.mapContactPreferences(caseParty);

        assertThat(result.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
        assertThat(result.getMobilePhoneNumber()).isEqualTo(MOBILE_NUMBER);
        assertThat(result.getContactByText()).isEqualTo(YesOrNo.YES);
    }

    @Test
    public void shouldMapContactPreferencesWhenNoPreferences() {
        CasePartyEntity caseParty = caseParty(
            null,
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList()
        );

        ContactPreferencesDto result = ApplicationMapper.mapContactPreferences(caseParty);

        assertThat(result.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
        assertThat(result.getMobilePhoneNumber()).isEqualTo(MOBILE_NUMBER);
        assertThat(result.getContactByText()).isNull();
    }

    @Test
    public void shouldMapTenantDetails() {
        CasePartyEntity caseParty = caseParty(
            null,
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList()
        );

        TenantDetailsDto result = ApplicationMapper.mapTenantDetails(caseParty);

        assertThat(result.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(result.getLastName()).isEqualTo(LAST_NAME);
        assertThat(result.getCompanyName()).isEqualTo(COMPANY_NAME);
        assertThat(result.getReferenceNumberForCommunications()).isEqualTo(REFERENCE_NUMBER);
    }

    @Test
    public void shouldMapTenantDetailsWhenFieldsNull() {
        CasePartyEntity caseParty = CasePartyEntity.builder().build();

        TenantDetailsDto result = ApplicationMapper.mapTenantDetails(caseParty);

        assertThat(result.getFirstName()).isNull();
        assertThat(result.getLastName()).isNull();
        assertThat(result.getCompanyName()).isNull();
        assertThat(result.getReferenceNumberForCommunications()).isNull();
    }

    @Test
    public void shouldMapHearingInspectionDetails() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .hearingRequested(YesOrNo.NO)
            .propertyInspections(List.of(
                PropertyInspectionEntity.builder()
                    .agreeToDecisionWithoutInspection(YesOrNo.YES)
                    .noDecisionWithoutInspectionReason("Some reason")
                    .build()
            ))
            .build();

        HearingInspectionDetailsDto result = ApplicationMapper.mapHearingInspectionDetails(ptCaseEntity);

        assertThat(result.getHearingRequested()).isEqualTo(YesOrNo.NO);
        assertThat(result.getAgreeToDecisionWithoutInspection()).isEqualTo(YesOrNo.YES);
        assertThat(result.getNoDecisionWithoutInspectionReason()).isEqualTo("Some reason");
    }

    @Test
    public void shouldMapHearingInspectionDetailsWhenNoPropertyInspections() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .hearingRequested(YesOrNo.YES)
            .propertyInspections(Collections.emptyList())
            .build();

        HearingInspectionDetailsDto result = ApplicationMapper.mapHearingInspectionDetails(ptCaseEntity);

        assertThat(result.getHearingRequested()).isEqualTo(YesOrNo.YES);
        assertThat(result.getAgreeToDecisionWithoutInspection()).isNull();
        assertThat(result.getNoDecisionWithoutInspectionReason()).isNull();
    }

    @Test
    public void shouldMapHearingInspectionDetailsWhenFieldsNull() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();

        HearingInspectionDetailsDto result = ApplicationMapper.mapHearingInspectionDetails(ptCaseEntity);

        assertThat(result.getHearingRequested()).isNull();
        assertThat(result.getAgreeToDecisionWithoutInspection()).isNull();
        assertThat(result.getNoDecisionWithoutInspectionReason()).isNull();
    }

    @Test
    public void shouldDefaultPostcodeToEmptyStringWhenNoProperties() {
        PTCaseEntity ptCase = ptCase(Collections.emptyList(), Collections.emptyList());
        CasePartyEntity caseParty = caseParty(
            ptCase,
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList()
        );
        CaseApplicationEntity entity = entityWithCaseType(caseParty, APPLICATION_TYPE);

        ApplicationDto result = ApplicationMapper.toDto(entity);

        assertThat(result.getPostcode()).isEqualTo("");
    }

    @Test
    public void shouldDefaultApplicationTypeToNullWhenCaseTypeIsNull() {
        CasePartyEntity caseParty = caseParty(
            ptCase(addresses(POSTCODE), Collections.emptyList()),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList()
        );
        CaseApplicationEntity entity = entityWithCaseType(caseParty, null);

        ApplicationDto result = ApplicationMapper.toDto(entity);

        assertThat(result.getApplicationType()).isNull();
    }

    @Test
    public void shouldDefaultApplicantIdamUserIdToNullWhenNoAccessRecords() {
        CasePartyEntity caseParty = caseParty(
            ptCase(addresses(POSTCODE), Collections.emptyList()),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList()
        );
        CaseApplicationEntity entity = entityWithCaseType(caseParty, APPLICATION_TYPE);

        ApplicationDto result = ApplicationMapper.toDto(entity);

        assertThat(result.getApplicantIdamUserId()).isNull();
    }

    @Test
    public void shouldMapNoticeOfRentChangeDetails() {
        DocumentEntity rentNoticeDoc = DocumentEntity.builder()
            .documentType(DocumentType.NEW_RENT_INCREASE_NOTICE)
            .url("http://dm-store/doc/1")
            .binaryUrl("http://dm-store/doc/1/binary")
            .fileName("rent_notice.pdf")
            .contentType("application/pdf")
            .size(1024L)
            .build();

        DocumentEntity invalidNoticeDoc = DocumentEntity.builder()
            .documentType(DocumentType.NOTICE_NOT_LEGALLY_VALID_EVIDENCE)
            .url("http://dm-store/doc/2")
            .binaryUrl("http://dm-store/doc/2/binary")
            .fileName("invalid_notice.pdf")
            .contentType("application/pdf")
            .size(2048L)
            .build();

        DocumentEntity hardshipDoc = DocumentEntity.builder()
            .documentType(DocumentType.HARDSHIP_EVIDENCE)
            .url("http://dm-store/doc/3")
            .binaryUrl("http://dm-store/doc/3/binary")
            .fileName("hardship.pdf")
            .contentType("application/pdf")
            .size(4096L)
            .build();

        NoticeOfRentChangeEntity noticeOfRentChange = NoticeOfRentChangeEntity.builder()
            .receivedLandlordNoticeProposingNewRent(YesOrNo.YES)
            .noUploadOfNoticeProposingNewRentReason("Paper copy only")
            .noticeLegallyValid(YesOrNo.NO)
            .noticeNotLegallyValidDetails("Incorrect notice period")
            .rentIncreaseToCauseHardship(YesOrNo.YES)
            .build();

        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .noticeOfRentChanges(List.of(noticeOfRentChange))
            .documents(List.of(rentNoticeDoc, invalidNoticeDoc, hardshipDoc))
            .build();

        NoticeOfRentIncreaseDto result = ApplicationMapper.mapNoticeOfRentChangeDetails(ptCaseEntity);

        assertThat(result.getReceivedLandlordNoticeProposingNewRent()).isEqualTo(YesOrNo.YES);
        assertThat(result.getNoUploadOfNoticeProposingNewRentReason()).isEqualTo("Paper copy only");
        assertThat(result.getNoticeLegallyValid()).isEqualTo(YesOrNo.NO);
        assertThat(result.getNoticeNotLegallyValidDetails()).isEqualTo("Incorrect notice period");
        assertThat(result.getRentIncreaseToCauseHardship()).isEqualTo(YesOrNo.YES);

        assertThat(result.getLandlordNoticeProposingNewRentDocument()).isEqualTo(
            DocumentDto.builder()
                .url("http://dm-store/doc/1")
                .binaryUrl("http://dm-store/doc/1/binary")
                .filename("rent_notice.pdf")
                .contentType("application/pdf")
                .size(1024L)
                .build()
        );
        assertThat(result.getNoticeNotLegallyValidDocument()).isEqualTo(
            DocumentDto.builder()
                .url("http://dm-store/doc/2")
                .binaryUrl("http://dm-store/doc/2/binary")
                .filename("invalid_notice.pdf")
                .contentType("application/pdf")
                .size(2048L)
                .build()
        );
        assertThat(result.getRentIncreaseToCauseHardshipDocument()).isEqualTo(
            DocumentDto.builder()
                .url("http://dm-store/doc/3")
                .binaryUrl("http://dm-store/doc/3/binary")
                .filename("hardship.pdf")
                .contentType("application/pdf")
                .size(4096L)
                .build()
        );
    }

    @Test
    public void shouldMapNoticeOfRentChangeDetailsWhenNoNoticeOfRentChanges() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .noticeOfRentChanges(Collections.emptyList())
            .build();

        NoticeOfRentIncreaseDto result = ApplicationMapper.mapNoticeOfRentChangeDetails(ptCaseEntity);

        assertThat(result).isNotNull();
        assertThat(result.getReceivedLandlordNoticeProposingNewRent()).isNull();
        assertThat(result.getNoUploadOfNoticeProposingNewRentReason()).isNull();
        assertThat(result.getNoticeLegallyValid()).isNull();
        assertThat(result.getNoticeNotLegallyValidDetails()).isNull();
        assertThat(result.getRentIncreaseToCauseHardship()).isNull();
        assertThat(result.getLandlordNoticeProposingNewRentDocument()).isNull();
        assertThat(result.getNoticeNotLegallyValidDocument()).isNull();
        assertThat(result.getRentIncreaseToCauseHardshipDocument()).isNull();
    }

    @Test
    public void shouldMapNoticeOfRentChangeDetailsWhenNoDocuments() {
        NoticeOfRentChangeEntity noticeOfRentChange = NoticeOfRentChangeEntity.builder()
            .receivedLandlordNoticeProposingNewRent(YesOrNo.YES)
            .noticeLegallyValid(YesOrNo.YES)
            .rentIncreaseToCauseHardship(YesOrNo.NO)
            .build();

        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .noticeOfRentChanges(List.of(noticeOfRentChange))
            .documents(Collections.emptyList())
            .build();

        NoticeOfRentIncreaseDto result = ApplicationMapper.mapNoticeOfRentChangeDetails(ptCaseEntity);

        assertThat(result.getReceivedLandlordNoticeProposingNewRent()).isEqualTo(YesOrNo.YES);
        assertThat(result.getNoticeLegallyValid()).isEqualTo(YesOrNo.YES);
        assertThat(result.getRentIncreaseToCauseHardship()).isEqualTo(YesOrNo.NO);
        assertThat(result.getLandlordNoticeProposingNewRentDocument()).isNull();
        assertThat(result.getNoticeNotLegallyValidDocument()).isNull();
        assertThat(result.getRentIncreaseToCauseHardshipDocument()).isNull();
    }

    @Test
    public void shouldMapPropertyDetails() {
        DocumentEntity floorPlanDoc = DocumentEntity.builder()
            .documentType(DocumentType.FLOOR_PLAN)
            .url("http://dm-store/doc/floor")
            .binaryUrl("http://dm-store/doc/floor/binary")
            .fileName("floor.pdf")
            .contentType("application/pdf")
            .size(1000L)
            .build();

        DocumentEntity outsidePropertyDoc = DocumentEntity.builder()
            .documentType(DocumentType.OUTSIDE_PROPERTY)
            .url("http://dm-store/doc/outside")
            .binaryUrl("http://dm-store/doc/outside/binary")
            .fileName("outside.pdf")
            .contentType("application/pdf")
            .size(2000L)
            .build();

        DocumentEntity roomDoc1 = DocumentEntity.builder()
            .documentType(DocumentType.PROPERTY_ROOMS)
            .url("http://dm-store/doc/room1")
            .binaryUrl("http://dm-store/doc/room1/binary")
            .fileName("room1.pdf")
            .contentType("application/pdf")
            .size(3000L)
            .build();

        DocumentEntity roomDoc2 = DocumentEntity.builder()
            .documentType(DocumentType.PROPERTY_ROOMS)
            .url("http://dm-store/doc/room2")
            .binaryUrl("http://dm-store/doc/room2/binary")
            .fileName("room2.pdf")
            .contentType("application/pdf")
            .size(3500L)
            .build();

        DocumentEntity repairsDoc = DocumentEntity.builder()
            .documentType(DocumentType.REPAIRS_EVIDENCE)
            .url("http://dm-store/doc/repairs")
            .binaryUrl("http://dm-store/doc/repairs/binary")
            .fileName("repairs.pdf")
            .contentType("application/pdf")
            .size(4000L)
            .build();

        TenancyDetailsEntity tenancyDetails = TenancyDetailsEntity.builder()
            .tenancyIncludeFacilities(YesOrNo.YES)
            .otherFacilitiesDetails("Parking and garden")
            .furnitureProvidedInTenancy(YesOrNo.YES)
            .furnitureProvidedInTenancyDetails("Bed, table")
            .additionalServicesProvidedInTenancy(YesOrNo.NO)
            .additionalServicesProvidedInTenancyDetails("None")
            .landlordRepairsDetails("Roof leak")
            .tenantRepairsDetails("Painted wall")
            .anyTenantsMadePropertyRepairs(YesNoNotSure.YES)
            .build();

        MarketRentCaseEntity marketRentCase = MarketRentCaseEntity.builder()
            .typeOfPropertyRenting(PropertyType.TERRACED_HOUSE)
            .rentingFlatDetails("Flat details")
            .rentingRoomDetails("Room details")
            .otherMethodOfRentDetails("Other details")
            .propertyFloorPlanAvailable(YesOrNo.YES)
            .floorplanManualDetails("Manual plan details")
            .propertyIndoorFeatures("Indoor features")
            .sharePropertyWithLandlord(YesOrNo.NO)
            .sharePropertyWithLandlordDetails("No sharing")
            .build();

        AddressEntity address = AddressEntity.builder()
            .addressLine1("123 Test Street")
            .addressLine2("Apt 4")
            .postTown("Manchester")
            .county("Greater Manchester")
            .postcode("M1 1AA")
            .build();

        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .tenancyDetails(List.of(tenancyDetails))
            .marketRentCases(List.of(marketRentCase))
            .documents(List.of(floorPlanDoc, outsidePropertyDoc, roomDoc1, roomDoc2, repairsDoc))
            .build();

        CasePartyEntity party = CasePartyEntity.builder()
            .addresses(List.of(address))
            .build();

        PropertyDetailsDto result = ApplicationMapper.mapPropertyDetails(ptCaseEntity, party);

        assertThat(result).isNotNull();
        assertThat(result.getAddressLine1()).isEqualTo("123 Test Street");
        assertThat(result.getAddressLine2()).isEqualTo("Apt 4");
        assertThat(result.getPostTown()).isEqualTo("Manchester");
        assertThat(result.getCounty()).isEqualTo("Greater Manchester");
        assertThat(result.getPostcode()).isEqualTo("M1 1AA");
        assertThat(result.getPropertyType()).isEqualTo(PropertyType.TERRACED_HOUSE);
        assertThat(result.getRentingFlatDetails()).isEqualTo("Flat details");
        assertThat(result.getRentingRoomDetails()).isEqualTo("Room details");
        assertThat(result.getOtherMethodRentingDetails()).isEqualTo("Other details");
        assertThat(result.getPropertyFloorPlanAvailable()).isEqualTo(YesOrNo.YES);
        assertThat(result.getFloorPlanManualDetails()).isEqualTo("Manual plan details");
        assertThat(result.getIndoorFeatures()).isEqualTo("Indoor features");
        assertThat(result.getOtherFacilitiesAvailable()).isEqualTo(YesOrNo.YES);
        assertThat(result.getOtherFacilitiesDetails()).isEqualTo("Parking and garden");
        assertThat(result.getFurnitureProvidedInTenancy()).isEqualTo(YesOrNo.YES);
        assertThat(result.getFurnitureProvidedInTenancyDetails()).isEqualTo("Bed, table");
        assertThat(result.getAdditionalServicesProvidedInTenancy()).isEqualTo(YesOrNo.NO);
        assertThat(result.getAdditionalServicesProvidedInTenancyDetails()).isEqualTo("None");
        assertThat(result.getSharePropertyWithLandlord()).isEqualTo(YesOrNo.NO);
        assertThat(result.getSharePropertyWithLandlordDetails()).isEqualTo("No sharing");
        assertThat(result.getLandlordRepairsDetails()).isEqualTo("Roof leak");
        assertThat(result.getTenantRepairsDetails()).isEqualTo("Painted wall");
        assertThat(result.getAnyTenantsMadePropertyRepairs()).isEqualTo(YesNoNotSure.YES);

        assertThat(result.getFloorPlanDocument()).isEqualTo(
            DocumentDto.builder()
                .url("http://dm-store/doc/floor")
                .binaryUrl("http://dm-store/doc/floor/binary")
                .filename("floor.pdf")
                .contentType("application/pdf")
                .size(1000L)
                .build()
        );
        assertThat(result.getOutsidePropertyDocument()).isEqualTo(
            DocumentDto.builder()
                .url("http://dm-store/doc/outside")
                .binaryUrl("http://dm-store/doc/outside/binary")
                .filename("outside.pdf")
                .contentType("application/pdf")
                .size(2000L)
                .build()
        );
        assertThat(result.getRepairsEvidenceDocument()).isEqualTo(
            DocumentDto.builder()
                .url("http://dm-store/doc/repairs")
                .binaryUrl("http://dm-store/doc/repairs/binary")
                .filename("repairs.pdf")
                .contentType("application/pdf")
                .size(4000L)
                .build()
        );
        assertThat(result.getPropertyRoomsDocuments()).hasSize(2);
        assertThat(result.getPropertyRoomsDocuments().get(0).getFilename()).isEqualTo("room1.pdf");
        assertThat(result.getPropertyRoomsDocuments().get(1).getFilename()).isEqualTo("room2.pdf");
    }

    @Test
    public void shouldReturnNullWhenTenancyDetailsIsNull() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .tenancyDetails(Collections.emptyList())
            .marketRentCases(List.of(MarketRentCaseEntity.builder().build()))
            .build();
        CasePartyEntity party = CasePartyEntity.builder()
            .addresses(List.of(AddressEntity.builder().build()))
            .build();

        PropertyDetailsDto result = ApplicationMapper.mapPropertyDetails(ptCaseEntity, party);

        assertThat(result).isNull();
    }

    @Test
    public void shouldReturnNullWhenMarketRentCaseIsNull() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .tenancyDetails(List.of(TenancyDetailsEntity.builder().build()))
            .marketRentCases(Collections.emptyList())
            .build();
        CasePartyEntity party = CasePartyEntity.builder()
            .addresses(List.of(AddressEntity.builder().build()))
            .build();

        PropertyDetailsDto result = ApplicationMapper.mapPropertyDetails(ptCaseEntity, party);

        assertThat(result).isNull();
    }

    @Test
    public void shouldReturnNullWhenAddressIsNull() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .tenancyDetails(List.of(TenancyDetailsEntity.builder().build()))
            .marketRentCases(List.of(MarketRentCaseEntity.builder().build()))
            .build();
        CasePartyEntity party = CasePartyEntity.builder()
            .addresses(Collections.emptyList())
            .build();

        PropertyDetailsDto result = ApplicationMapper.mapPropertyDetails(ptCaseEntity, party);

        assertThat(result).isNull();
    }

    @Test
    public void shouldMapCurrentRentDetails() {
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime originalStartDate = LocalDateTime.of(2024, 1, 1, 0, 0);

        TenancyDetailsEntity tenancyDetails = TenancyDetailsEntity.builder()
            .tribunalPreviouslyDeterminedTenancyRent(YesOrNo.YES)
            .previousTribunalCaseReference("TRIB-123")
            .currentTenancyStartDate(startDate)
            .tenancyEndDate(endDate)
            .currentTenancyReplaceOriginalTenancy(YesNoNotSure.YES)
            .originalTenancyStartDate(originalStartDate)
            .build();

        MarketRentCaseEntity marketRentCase = MarketRentCaseEntity.builder()
            .rentPaymentFrequency(Frequency.MONTHLY)
            .rentCostWeekly(new BigDecimal("100.00"))
            .rentCostFortnightly(new BigDecimal("200.00"))
            .rentCostMonthly(new BigDecimal("400.00"))
            .rentCostYearly(new BigDecimal("4800.00"))
            .rentIncludesCouncilTax(YesOrNo.YES)
            .councilTaxFrequency(Frequency.MONTHLY)
            .councilTaxCostWeekly(new BigDecimal("25.00"))
            .councilTaxCostFortnightly(new BigDecimal("50.00"))
            .councilTaxCostMonthly(new BigDecimal("100.00"))
            .councilTaxCostYearly(new BigDecimal("1200.00"))
            .councilTaxFrequencyAndCostDetails("Council tax details")
            .utilitiesPaidFrequency(Frequency.MONTHLY)
            .utilitiesCostWeekly(new BigDecimal("15.00"))
            .utilitiesCostFortnightly(new BigDecimal("30.00"))
            .utilitiesCostMonthly(new BigDecimal("60.00"))
            .utilitiesCostYearly(new BigDecimal("720.00"))
            .utilitiesFrequencyAndCostDetails("Utilities details")
            .additionalRentalServiceChargesVary(YesOrNo.YES)
            .additionalRentalVaryingServiceChargesDetails("Service charge details")
            .build();

        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .tenancyDetails(List.of(tenancyDetails))
            .marketRentCases(List.of(marketRentCase))
            .build();

        CurrentRentsDetailsDto result = ApplicationMapper.mapCurrentRentDetails(ptCaseEntity);

        assertThat(result).isNotNull();
        assertThat(result.getTribunalPreviouslyDeterminedTenancyRent()).isEqualTo(YesOrNo.YES);
        assertThat(result.getPreviousTribunalCaseReference()).isEqualTo("TRIB-123");
        assertThat(result.getRentPaymentFrequency()).isEqualTo(Frequency.MONTHLY);
        assertThat(result.getRentCostWeekly()).isEqualTo(new BigDecimal("100.00"));
        assertThat(result.getRentCostFortnightly()).isEqualTo(new BigDecimal("200.00"));
        assertThat(result.getRentCostMonthly()).isEqualTo(new BigDecimal("400.00"));
        assertThat(result.getRentCostYearly()).isEqualTo(new BigDecimal("4800.00"));
        assertThat(result.getRentIncludesCouncilTax()).isEqualTo(YesOrNo.YES);
        assertThat(result.getCouncilTaxFrequency()).isEqualTo(Frequency.MONTHLY);
        assertThat(result.getCouncilTaxCostWeekly()).isEqualTo(new BigDecimal("25.00"));
        assertThat(result.getCouncilTaxCostFortnightly()).isEqualTo(new BigDecimal("50.00"));
        assertThat(result.getCouncilTaxCostMonthly()).isEqualTo(new BigDecimal("100.00"));
        assertThat(result.getCouncilTaxCostYearly()).isEqualTo(new BigDecimal("1200.00"));
        assertThat(result.getCouncilTaxFrequencyAndCostDetails()).isEqualTo("Council tax details");
        assertThat(result.getUtilitiesPaidFrequency()).isEqualTo(Frequency.MONTHLY);
        assertThat(result.getUtilitiesCostWeekly()).isEqualTo(new BigDecimal("15.00"));
        assertThat(result.getUtilitiesCostFortnightly()).isEqualTo(new BigDecimal("30.00"));
        assertThat(result.getUtilitiesCostMonthly()).isEqualTo(new BigDecimal("60.00"));
        assertThat(result.getUtilitiesCostYearly()).isEqualTo(new BigDecimal("720.00"));
        assertThat(result.getUtilitiesPaidFrequencyAndCostDetails()).isEqualTo("Utilities details");
        assertThat(result.getCurrentTenancyStartDate()).isEqualTo(startDate);
        assertThat(result.getCurrentTenancyEndDate()).isEqualTo(endDate);
        assertThat(result.getCurrentTenancyReplaceOriginalTenancy()).isEqualTo(YesNoNotSure.YES);
        assertThat(result.getOriginalTenancyStartDate()).isEqualTo(originalStartDate);
        assertThat(result.getAdditionalRentalServiceChargesVary()).isEqualTo(YesOrNo.YES);
        assertThat(result.getAdditionalRentalVaryingServiceChargesDetails()).isEqualTo("Service charge details");
    }

    @Test
    public void shouldReturnNullWhenTenancyDetailsIsNullForCurrentRentDetails() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .tenancyDetails(Collections.emptyList())
            .marketRentCases(List.of(MarketRentCaseEntity.builder().build()))
            .build();

        CurrentRentsDetailsDto result = ApplicationMapper.mapCurrentRentDetails(ptCaseEntity);

        assertThat(result).isNull();
    }

    @Test
    public void shouldReturnNullWhenMarketRentCaseIsNullForCurrentRentDetails() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .tenancyDetails(List.of(TenancyDetailsEntity.builder().build()))
            .marketRentCases(Collections.emptyList())
            .build();

        CurrentRentsDetailsDto result = ApplicationMapper.mapCurrentRentDetails(ptCaseEntity);

        assertThat(result).isNull();
    }

    @Test
    public void shouldMapMarketRentDetails() {
        DocumentEntity document = DocumentEntity.builder()
            .documentType(DocumentType.PROPOSED_RENT_EVIDENCE)
            .url("http://dm-store/doc/proposed-rent")
            .binaryUrl("http://dm-store/doc/proposed-rent/binary")
            .fileName("proposed-rent.pdf")
            .contentType("application/pdf")
            .size(1024L)
            .build();

        MarketRentCaseEntity marketRentCase = MarketRentCaseEntity.builder()
            .applicantSuggestedMonthlyMarketRent(new BigDecimal("1500.00"))
            .applicantSuggestedMonthlyMarketRentReasons("Similar properties in the area rent for this amount")
            .additionalPropertyInfoToConsiderWhenDetermining(YesOrNo.YES)
            .additionalPropertyInfoToConsiderWhenDeterminingDetails("Garden was recently renovated")
            .build();

        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .marketRentCases(List.of(marketRentCase))
            .documents(List.of(document))
            .build();

        MarketRentDto result = ApplicationMapper.mapMarketRentDetails(ptCaseEntity);

        assertThat(result).isNotNull();
        assertThat(result.getApplicantSuggestedMonthlyMarketRent()).isEqualTo(new BigDecimal("1500.00"));
        assertThat(result.getApplicantSuggestedMonthlyMarketRentReasons())
            .isEqualTo("Similar properties in the area rent for this amount");
        assertThat(result.getAdditionalPropertyInfoToConsiderWhenDetermining()).isEqualTo(YesOrNo.YES);
        assertThat(result.getAdditionalPropertyInfoToConsiderWhenDeterminingDetails())
            .isEqualTo("Garden was recently renovated");
        assertThat(result.getSuggestedMarketRentEvidence()).isNotNull();
        assertThat(result.getSuggestedMarketRentEvidence().getUrl()).isEqualTo("http://dm-store/doc/proposed-rent");
        assertThat(result.getSuggestedMarketRentEvidence().getBinaryUrl())
            .isEqualTo("http://dm-store/doc/proposed-rent/binary");
        assertThat(result.getSuggestedMarketRentEvidence().getFilename()).isEqualTo("proposed-rent.pdf");
        assertThat(result.getSuggestedMarketRentEvidence().getContentType()).isEqualTo("application/pdf");
        assertThat(result.getSuggestedMarketRentEvidence().getSize()).isEqualTo(1024L);
    }

    @Test
    public void shouldMapMarketRentDetailsWithoutEvidenceDocument() {
        MarketRentCaseEntity marketRentCase = MarketRentCaseEntity.builder()
            .applicantSuggestedMonthlyMarketRent(new BigDecimal("1500.00"))
            .applicantSuggestedMonthlyMarketRentReasons("Reasons")
            .additionalPropertyInfoToConsiderWhenDetermining(YesOrNo.NO)
            .build();

        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .marketRentCases(List.of(marketRentCase))
            .documents(Collections.emptyList())
            .build();

        MarketRentDto result = ApplicationMapper.mapMarketRentDetails(ptCaseEntity);

        assertThat(result).isNotNull();
        assertThat(result.getApplicantSuggestedMonthlyMarketRent()).isEqualTo(new BigDecimal("1500.00"));
        assertThat(result.getApplicantSuggestedMonthlyMarketRentReasons()).isEqualTo("Reasons");
        assertThat(result.getAdditionalPropertyInfoToConsiderWhenDetermining()).isEqualTo(YesOrNo.NO);
        assertThat(result.getAdditionalPropertyInfoToConsiderWhenDeterminingDetails()).isNull();
        assertThat(result.getSuggestedMarketRentEvidence()).isNull();
    }

    @Test
    public void shouldReturnNullWhenMarketRentCaseIsNullForMarketRentDetails() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .marketRentCases(Collections.emptyList())
            .build();

        MarketRentDto result = ApplicationMapper.mapMarketRentDetails(ptCaseEntity);

        assertThat(result).isNull();
    }

    @Test
    public void shouldMapTenancyAgreement() {
        DocumentEntity document = DocumentEntity.builder()
            .documentType(DocumentType.TENANCY_AGREEMENT)
            .url("http://dm-store/doc/tenancy-agreement")
            .binaryUrl("http://dm-store/doc/tenancy-agreement/binary")
            .fileName("tenancy-agreement.pdf")
            .contentType("application/pdf")
            .size(2048L)
            .build();

        TenancyDetailsEntity tenancyDetails = TenancyDetailsEntity.builder()
            .copyOfTenancyAgreement(YesOrNo.YES)
            .noTenancyAgreementReason("Reason")
            .build();

        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .tenancyDetails(List.of(tenancyDetails))
            .documents(List.of(document))
            .build();

        TenancyAgreementDto result = ApplicationMapper.mapTenancyAgreement(ptCaseEntity);

        assertThat(result).isNotNull();
        assertThat(result.getCopyOfTenancyAgreement()).isEqualTo(YesOrNo.YES);
        assertThat(result.getNoTenancyAgreementReason()).isEqualTo("Reason");
        assertThat(result.getTenancyAgreementEvidence()).isNotNull();
        assertThat(result.getTenancyAgreementEvidence().getUrl()).isEqualTo("http://dm-store/doc/tenancy-agreement");
        assertThat(result.getTenancyAgreementEvidence().getBinaryUrl())
            .isEqualTo("http://dm-store/doc/tenancy-agreement/binary");
        assertThat(result.getTenancyAgreementEvidence().getFilename()).isEqualTo("tenancy-agreement.pdf");
        assertThat(result.getTenancyAgreementEvidence().getContentType()).isEqualTo("application/pdf");
        assertThat(result.getTenancyAgreementEvidence().getSize()).isEqualTo(2048L);
    }

    @Test
    public void shouldMapTenancyAgreementWithoutEvidenceDocument() {
        TenancyDetailsEntity tenancyDetails = TenancyDetailsEntity.builder()
            .copyOfTenancyAgreement(YesOrNo.NO)
            .noTenancyAgreementReason("No agreement available")
            .build();

        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .tenancyDetails(List.of(tenancyDetails))
            .documents(Collections.emptyList())
            .build();

        TenancyAgreementDto result = ApplicationMapper.mapTenancyAgreement(ptCaseEntity);

        assertThat(result).isNotNull();
        assertThat(result.getCopyOfTenancyAgreement()).isEqualTo(YesOrNo.NO);
        assertThat(result.getNoTenancyAgreementReason()).isEqualTo("No agreement available");
        assertThat(result.getTenancyAgreementEvidence()).isNull();
    }

    @Test
    public void shouldReturnNullWhenTenancyDetailsIsNullForTenancyAgreement() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .tenancyDetails(Collections.emptyList())
            .build();

        TenancyAgreementDto result = ApplicationMapper.mapTenancyAgreement(ptCaseEntity);

        assertThat(result).isNull();
    }

    @Test
    public void shouldMapDocument() {
        DocumentEntity entity = DocumentEntity.builder()
            .url("http://dm-store/doc/1")
            .binaryUrl("http://dm-store/doc/1/binary")
            .fileName("file.pdf")
            .contentType("application/pdf")
            .size(5000L)
            .build();

        DocumentDto result = ApplicationMapper.mapDocument(entity);

        assertThat(result.getUrl()).isEqualTo("http://dm-store/doc/1");
        assertThat(result.getBinaryUrl()).isEqualTo("http://dm-store/doc/1/binary");
        assertThat(result.getFilename()).isEqualTo("file.pdf");
        assertThat(result.getContentType()).isEqualTo("application/pdf");
        assertThat(result.getSize()).isEqualTo(5000L);
    }

    private static List<AddressEntity> addresses(String postcode) {
        return List.of(AddressEntity.builder()
            .addressLine1("123 Test St")
            .postTown("London")
            .postcode(postcode)
            .build());
    }

    private static List<TenancyDetailsEntity> tenancyDetails(TenancyType tenancyType) {
        return List.of(TenancyDetailsEntity.builder()
            .tenancyType(tenancyType)
            .copyOfTenancyAgreement(YesOrNo.YES)
            .noTenancyAgreementReason("No agreement reason")
            .build());
    }

    private static List<MarketRentCaseEntity> marketRentCases() {
        return List.of(MarketRentCaseEntity.builder()
            .typeOfPropertyRenting(PropertyType.TERRACED_HOUSE)
            .applicantSuggestedMonthlyMarketRent(new BigDecimal("1200.00"))
            .applicantSuggestedMonthlyMarketRentReasons("Market rate for the area")
            .additionalPropertyInfoToConsiderWhenDetermining(YesOrNo.YES)
            .additionalPropertyInfoToConsiderWhenDeterminingDetails("Recently refurbished")
            .build());
    }

    private static PTCaseEntity ptCase(List<AddressEntity> addresses, List<TenancyDetailsEntity> tenancyDetails) {
        return PTCaseEntity.builder()
            .caseReference(CASE_REFERENCE)
            .hearingRequested(YesOrNo.YES)
            .propertyInspections(List.of(
                PropertyInspectionEntity.builder()
                    .agreeToDecisionWithoutInspection(YesOrNo.YES)
                    .noDecisionWithoutInspectionReason("Inspection reason")
                    .build()
            ))
            .addresses(addresses)
            .tenancyDetails(tenancyDetails)
            .marketRentCases(marketRentCases())
            .documents(List.of(
                DocumentEntity.builder()
                    .documentType(DocumentType.TENANCY_AGREEMENT)
                    .url("http://dm-store/doc/tenancy-agreement")
                    .binaryUrl("http://dm-store/doc/tenancy-agreement/binary")
                    .fileName("tenancy-agreement.pdf")
                    .contentType("application/pdf")
                    .size(2048L)
                    .build()
            ))
            .build();
    }

    private static CasePartyEntity caseParty(
        PTCaseEntity ptCase,
        List<CasePartyAccessEntity> access,
        List<CasePartyContactPreferenceEntity> contactPreferences,
        List<AddressEntity> addresses
    ) {
        return CasePartyEntity.builder()
            .firstName(FIRST_NAME)
            .lastName(LAST_NAME)
            .organisationName(COMPANY_NAME)
            .referenceNumber(REFERENCE_NUMBER)
            .emailAddress(EMAIL)
            .phoneNumber(PHONE_NUMBER)
            .mobilePhoneNumber(MOBILE_NUMBER)
            .ptCase(ptCase)
            .access(access)
            .contactPreferences(contactPreferences)
            .addresses(addresses)
            .build();
    }

    private static List<CasePartyContactPreferenceEntity> contactPreferences(YesOrNo text, YesOrNo phone) {
        return List.of(CasePartyContactPreferenceEntity.builder()
            .contactByText(text)
            .build());
    }

    private static CaseApplicationEntity entityWithCaseType(
        CasePartyEntity caseParty,
        ApplicationType applicationType
    ) {
        return CaseApplicationEntity.builder()
            .caseParty(caseParty)
            .caseType(
                applicationType != null
                    ? CaseTypeEntity.builder().applicationTypeName(applicationType).build()
                    : null)
            .createdDate(CREATED_DATE)
            .build();
    }

    private static CaseApplicationEntity fullEntity(UUID userId) {
        List<AddressEntity> partyAddresses = addresses(POSTCODE);
        PTCaseEntity ptCase = ptCase(partyAddresses, tenancyDetails(TENANCY_TYPE));
        List<CasePartyAccessEntity> access = List.of(
            CasePartyAccessEntity.builder().idamId(userId).build()
        );
        CasePartyEntity caseParty = caseParty(
            ptCase,
            access,
            contactPreferences(YesOrNo.YES, YesOrNo.NO),
            partyAddresses
        );
        return entityWithCaseType(caseParty, APPLICATION_TYPE);
    }
}
