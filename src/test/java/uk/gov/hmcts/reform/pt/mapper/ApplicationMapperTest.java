package uk.gov.hmcts.reform.pt.mapper;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicationType;
import uk.gov.hmcts.reform.pt.ccd.domain.DocumentType;
import uk.gov.hmcts.reform.pt.ccd.domain.Frequency;
import uk.gov.hmcts.reform.pt.ccd.domain.LandlordRepresentativeType;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyRole;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyType;
import uk.gov.hmcts.reform.pt.ccd.domain.TenancyType;
import uk.gov.hmcts.reform.pt.ccd.domain.YesNoNotSure;
import uk.gov.hmcts.reform.pt.dto.ApplicationDto;
import uk.gov.hmcts.reform.pt.dto.ContactPreferencesDto;
import uk.gov.hmcts.reform.pt.dto.CurrentRentsDetailsDto;
import uk.gov.hmcts.reform.pt.dto.DocumentDto;
import uk.gov.hmcts.reform.pt.dto.HearingInspectionDetailsDto;
import uk.gov.hmcts.reform.pt.dto.LandlordDetailsDto;
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
import uk.gov.hmcts.reform.pt.entity.CasePartyRoleEntity;
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

class ApplicationMapperTest {

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
    void shouldMapToDtoFromEntity() {
        UUID userId = UUID.randomUUID();
        CaseApplicationEntity entity = fullEntity(userId);

        ApplicationDto result = ApplicationMapper.toDto(entity);

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expectedDto(userId));
    }

    @Test
    void shouldThrowCasePartyNotFoundExceptionWhenCasePartyIsNull() {
        CaseApplicationEntity entity = CaseApplicationEntity.builder()
            .caseParty(null)
            .build();

        assertThatThrownBy(() -> ApplicationMapper.toDto(entity))
            .isInstanceOf(CasePartyNotFoundException.class)
            .hasMessageContaining("Case party not found for application");
    }

    @Test
    void shouldThrowCaseNotFoundExceptionWhenPtCaseIsNull() {
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
    void shouldMapContactPreferences() {
        CasePartyEntity caseParty = caseParty(
            null,
            Collections.emptyList(),
            contactPreferences(YesOrNo.YES),
            Collections.emptyList()
        );

        ContactPreferencesDto result = ApplicationMapper.mapContactPreferences(caseParty);

        assertThat(result.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
        assertThat(result.getMobilePhoneNumber()).isEqualTo(MOBILE_NUMBER);
        assertThat(result.getContactByText()).isEqualTo(YesOrNo.YES);
    }

    @Test
    void shouldMapContactPreferencesWhenNoPreferences() {
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
    void shouldMapTenantDetails() {
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
    void shouldMapTenantDetailsWhenFieldsNull() {
        CasePartyEntity caseParty = CasePartyEntity.builder().build();

        TenantDetailsDto result = ApplicationMapper.mapTenantDetails(caseParty);

        assertThat(result.getFirstName()).isNull();
        assertThat(result.getLastName()).isNull();
        assertThat(result.getCompanyName()).isNull();
        assertThat(result.getReferenceNumberForCommunications()).isNull();
    }

    @Test
    void shouldMapHearingInspectionDetails() {
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
    void shouldMapHearingInspectionDetailsWhenNoPropertyInspections() {
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
    void shouldMapHearingInspectionDetailsWhenFieldsNull() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder().build();

        HearingInspectionDetailsDto result = ApplicationMapper.mapHearingInspectionDetails(ptCaseEntity);

        assertThat(result.getHearingRequested()).isNull();
        assertThat(result.getAgreeToDecisionWithoutInspection()).isNull();
        assertThat(result.getNoDecisionWithoutInspectionReason()).isNull();
    }

    @Test
    void shouldDefaultPostcodeToEmptyStringWhenNoProperties() {
        PTCaseEntity ptCase = ptCase(Collections.emptyList(), Collections.emptyList());
        CasePartyEntity caseParty = caseParty(
            ptCase,
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList()
        );
        CaseApplicationEntity entity = entityWithCaseType(caseParty, APPLICATION_TYPE);

        ApplicationDto result = ApplicationMapper.toDto(entity);

        assertThat(result.getPostcode()).isEmpty();
    }

    @Test
    void shouldDefaultApplicationTypeToNullWhenCaseTypeIsNull() {
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
    void shouldDefaultApplicantIdamUserIdToNullWhenNoAccessRecords() {
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
    void shouldMapNoticeOfRentChangeDetails() {
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
    void shouldMapNoticeOfRentChangeDetailsWhenNoNoticeOfRentChanges() {
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
    void shouldMapNoticeOfRentChangeDetailsWhenNoDocuments() {
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
    void shouldMapPropertyDetails() {
        DocumentEntity floorPlanDoc = DocumentEntity.builder()
            .documentType(DocumentType.PROPERTY_FLOOR_PLAN)
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
            .documentType(DocumentType.TENANT_REPAIRS_EVIDENCE)
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

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expectedPropertyDetailsDto());
    }

    @Test
    void shouldMapPropertyDetailsWhenTenancyDetailsIsNull() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .tenancyDetails(Collections.emptyList())
            .marketRentCases(List.of(MarketRentCaseEntity.builder().build()))
            .build();
        CasePartyEntity party = CasePartyEntity.builder()
            .addresses(List.of(AddressEntity.builder().build()))
            .build();

        PropertyDetailsDto result = ApplicationMapper.mapPropertyDetails(ptCaseEntity, party);

        assertThat(result).isNotNull();
        assertThat(result.getOtherFacilitiesAvailable()).isNull();
        assertThat(result.getLandlordRepairsDetails()).isNull();
    }

    @Test
    void shouldMapPropertyDetailsWhenMarketRentCaseIsNull() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .tenancyDetails(List.of(TenancyDetailsEntity.builder().build()))
            .marketRentCases(Collections.emptyList())
            .build();
        CasePartyEntity party = CasePartyEntity.builder()
            .addresses(List.of(AddressEntity.builder().build()))
            .build();

        PropertyDetailsDto result = ApplicationMapper.mapPropertyDetails(ptCaseEntity, party);

        assertThat(result).isNotNull();
        assertThat(result.getPropertyType()).isNull();
        assertThat(result.getSharePropertyWithLandlord()).isNull();
    }

    @Test
    void shouldMapPropertyDetailsWhenAddressIsNull() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .tenancyDetails(List.of(TenancyDetailsEntity.builder().build()))
            .marketRentCases(List.of(MarketRentCaseEntity.builder().build()))
            .build();
        CasePartyEntity party = CasePartyEntity.builder()
            .addresses(Collections.emptyList())
            .build();

        PropertyDetailsDto result = ApplicationMapper.mapPropertyDetails(ptCaseEntity, party);

        assertThat(result).isNotNull();
        assertThat(result.getAddressLine1()).isNull();
        assertThat(result.getPostcode()).isNull();
    }

    @Test
    void shouldReturnNullWhenNoPropertyEntitiesAndNoDocumentsExist() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .tenancyDetails(Collections.emptyList())
            .marketRentCases(Collections.emptyList())
            .documents(Collections.emptyList())
            .build();
        CasePartyEntity party = CasePartyEntity.builder()
            .addresses(Collections.emptyList())
            .build();

        PropertyDetailsDto result = ApplicationMapper.mapPropertyDetails(ptCaseEntity, party);

        assertThat(result).isNull();
    }

    @Test
    void shouldMapPropertyDocumentsWhenNoOtherPropertyEntitiesExist() {
        DocumentEntity floorPlan = DocumentEntity.builder()
            .documentType(DocumentType.PROPERTY_FLOOR_PLAN)
            .url("http://cdam/cases/documents/abc")
            .binaryUrl("http://cdam/cases/documents/abc/binary")
            .fileName("floor-plan.pdf")
            .contentType("application/pdf")
            .size(1024L)
            .build();

        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .tenancyDetails(Collections.emptyList())
            .marketRentCases(Collections.emptyList())
            .documents(List.of(floorPlan))
            .build();
        CasePartyEntity party = CasePartyEntity.builder()
            .addresses(Collections.emptyList())
            .build();

        PropertyDetailsDto result = ApplicationMapper.mapPropertyDetails(ptCaseEntity, party);

        assertThat(result).isNotNull();
        assertThat(result.getFloorPlanDocument()).isNotNull();
        assertThat(result.getFloorPlanDocument().getUrl()).isEqualTo("http://cdam/cases/documents/abc");
        assertThat(result.getFloorPlanDocument().getFilename()).isEqualTo("floor-plan.pdf");
        assertThat(result.getAddressLine1()).isNull();
        assertThat(result.getPropertyType()).isNull();
    }

    @Test
    void shouldMapCurrentRentDetails() {
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
            .rentInclusiveOfUtilityCharges(YesOrNo.YES)
            .additionalRentalServiceChargesVary(YesOrNo.YES)
            .varyingAdditionalRentalServiceChargesDetails("Service charge details")
            .build();

        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .tenancyDetails(List.of(tenancyDetails))
            .marketRentCases(List.of(marketRentCase))
            .build();

        CurrentRentsDetailsDto result = ApplicationMapper.mapCurrentRentDetails(ptCaseEntity);

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expectedCurrentRentsDetailsDto(startDate, endDate, originalStartDate));
    }

    @Test
    void shouldMapCurrentRentDetailsWhenTenancyDetailsIsNull() {
        MarketRentCaseEntity marketRentCase = MarketRentCaseEntity.builder()
            .rentPaymentFrequency(Frequency.MONTHLY)
            .rentCostMonthly(new BigDecimal("400.00"))
            .build();
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .tenancyDetails(Collections.emptyList())
            .marketRentCases(List.of(marketRentCase))
            .build();

        CurrentRentsDetailsDto result = ApplicationMapper.mapCurrentRentDetails(ptCaseEntity);

        assertThat(result).isNotNull();
        assertThat(result.getRentPaymentFrequency()).isEqualTo(Frequency.MONTHLY);
        assertThat(result.getRentCostMonthly()).isEqualTo(new BigDecimal("400.00"));
        assertThat(result.getTribunalPreviouslyDeterminedTenancyRent()).isNull();
        assertThat(result.getCurrentTenancyStartDate()).isNull();
    }

    @Test
    void shouldMapCurrentRentDetailsWhenMarketRentCaseIsNull() {
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        TenancyDetailsEntity tenancyDetails = TenancyDetailsEntity.builder()
            .tribunalPreviouslyDeterminedTenancyRent(YesOrNo.YES)
            .currentTenancyStartDate(startDate)
            .build();

        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .tenancyDetails(List.of(tenancyDetails))
            .marketRentCases(Collections.emptyList())
            .build();

        CurrentRentsDetailsDto result = ApplicationMapper.mapCurrentRentDetails(ptCaseEntity);

        assertThat(result).isNotNull();
        assertThat(result.getTribunalPreviouslyDeterminedTenancyRent()).isEqualTo(YesOrNo.YES);
        assertThat(result.getCurrentTenancyStartDate()).isEqualTo(startDate);
        assertThat(result.getRentPaymentFrequency()).isNull();
        assertThat(result.getRentCostMonthly()).isNull();
    }

    @Test
    void shouldReturnNullWhenNoCurrentRentEntitiesExist() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .tenancyDetails(Collections.emptyList())
            .marketRentCases(Collections.emptyList())
            .build();

        CurrentRentsDetailsDto result = ApplicationMapper.mapCurrentRentDetails(ptCaseEntity);

        assertThat(result).isNull();
    }

    @Test
    void shouldMapMarketRentDetails() {
        DocumentEntity document = DocumentEntity.builder()
            .documentType(DocumentType.TENANT_PROPOSED_MARKET_RENT_EVIDENCE)
            .url("http://dm-store/doc/proposed-rent")
            .binaryUrl("http://dm-store/doc/proposed-rent/binary")
            .fileName("proposed-rent.pdf")
            .contentType("application/pdf")
            .size(1024L)
            .build();

        MarketRentCaseEntity marketRentCase = MarketRentCaseEntity.builder()
            .applicantSuggestedMonthlyMarketRent(new BigDecimal("1500.00"))
            .applicantSuggestedMonthlyMarketRentReasons("Similar properties in the area rent for this amount")
            .additionalPropertyInfoToConsiderWhenDeterminingRent(YesOrNo.YES)
            .additionalPropertyInfoToConsiderWhenDeterminingRentDetails("Renovations")
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
            .isEqualTo("Renovations");
        assertThat(result.getSuggestedMarketRentEvidence()).isNotNull();
        assertThat(result.getSuggestedMarketRentEvidence().getUrl()).isEqualTo("http://dm-store/doc/proposed-rent");
        assertThat(result.getSuggestedMarketRentEvidence().getBinaryUrl())
            .isEqualTo("http://dm-store/doc/proposed-rent/binary");
        assertThat(result.getSuggestedMarketRentEvidence().getFilename()).isEqualTo("proposed-rent.pdf");
        assertThat(result.getSuggestedMarketRentEvidence().getContentType()).isEqualTo("application/pdf");
        assertThat(result.getSuggestedMarketRentEvidence().getSize()).isEqualTo(1024L);
    }

    @Test
    void shouldMapMarketRentDetailsWithoutEvidenceDocument() {
        MarketRentCaseEntity marketRentCase = MarketRentCaseEntity.builder()
            .applicantSuggestedMonthlyMarketRent(new BigDecimal("1500.00"))
            .applicantSuggestedMonthlyMarketRentReasons("Reasons")
            .additionalPropertyInfoToConsiderWhenDeterminingRent(YesOrNo.NO)
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
    void shouldReturnNullWhenMarketRentCaseIsNullForMarketRentDetails() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .marketRentCases(Collections.emptyList())
            .build();

        MarketRentDto result = ApplicationMapper.mapMarketRentDetails(ptCaseEntity);

        assertThat(result).isNull();
    }

    @Test
    void shouldMapTenancyAgreement() {
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
        assertThat(result.getTenancyAgreementDocument()).isNotNull();
        assertThat(result.getTenancyAgreementDocument().getUrl()).isEqualTo("http://dm-store/doc/tenancy-agreement");
        assertThat(result.getTenancyAgreementDocument().getBinaryUrl())
            .isEqualTo("http://dm-store/doc/tenancy-agreement/binary");
        assertThat(result.getTenancyAgreementDocument().getFilename()).isEqualTo("tenancy-agreement.pdf");
        assertThat(result.getTenancyAgreementDocument().getContentType()).isEqualTo("application/pdf");
        assertThat(result.getTenancyAgreementDocument().getSize()).isEqualTo(2048L);
    }

    @Test
    void shouldMapNoticeDocumentsWhenNoNoticeOfRentChangeExists() {
        DocumentEntity notice = DocumentEntity.builder()
            .documentType(DocumentType.NEW_RENT_INCREASE_NOTICE)
            .url("http://cdam/cases/documents/def")
            .binaryUrl("http://cdam/cases/documents/def/binary")
            .fileName("notice.pdf")
            .build();

        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .noticeOfRentChanges(Collections.emptyList())
            .documents(List.of(notice))
            .build();

        NoticeOfRentIncreaseDto result = ApplicationMapper.mapNoticeOfRentChangeDetails(ptCaseEntity);

        assertThat(result).isNotNull();
        assertThat(result.getLandlordNoticeProposingNewRentDocument()).isNotNull();
        assertThat(result.getLandlordNoticeProposingNewRentDocument().getUrl())
            .isEqualTo("http://cdam/cases/documents/def");
        assertThat(result.getReceivedLandlordNoticeProposingNewRent()).isNull();
    }

    @Test
    void shouldMapTenancyAgreementWithoutEvidenceDocument() {
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
        assertThat(result.getTenancyAgreementDocument()).isNull();
    }

    @Test
    void shouldReturnNullWhenTenancyDetailsIsNullForTenancyAgreement() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .tenancyDetails(Collections.emptyList())
            .build();

        TenancyAgreementDto result = ApplicationMapper.mapTenancyAgreement(ptCaseEntity);

        assertThat(result).isNull();
    }

    @Test
    void shouldMapDocument() {
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

    @Test
    void shouldMapLandlordDetails() {
        CasePartyRoleEntity landlordRole = CasePartyRoleEntity.builder().roleName(PartyRole.LANDLORD).build();
        CasePartyRoleEntity agentRole = CasePartyRoleEntity.builder().roleName(PartyRole.LETTING_AGENT).build();
        CasePartyRoleEntity repRole = CasePartyRoleEntity.builder().roleName(PartyRole.LANDLORD_REPRESENTATIVE).build();

        AddressEntity landlordAddress = AddressEntity.builder()
            .addressLine1("1 Landlord Way")
            .addressLine2("Somewhere")
            .postTown("London")
            .county("Greater London")
            .postcode("SW1 1AA")
            .build();

        CasePartyEntity landlordParty = CasePartyEntity.builder()
            .firstName("Lord")
            .lastName("Landlord")
            .organisationName("Landlord Estates")
            .emailAddress("landlord@example.com")
            .phoneNumber("0123456789")
            .referenceNumber("LL01")
            .role(landlordRole)
            .addresses(List.of(landlordAddress))
            .build();

        CasePartyEntity agentParty = CasePartyEntity.builder()
            .firstName("Agent")
            .lastName("Smith")
            .role(agentRole)
            .addresses(Collections.emptyList())
            .build();

        CasePartyEntity repParty = CasePartyEntity.builder()
            .firstName("Rep")
            .lastName("Jones")
            .role(repRole)
            .addresses(Collections.emptyList())
            .build();

        CasePartyEntity partyWithoutRole = CasePartyEntity.builder()
            .firstName("NoRole")
            .build();

        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .landlordType(LandlordRepresentativeType.LETTING_AGENT)
            .parties(List.of(partyWithoutRole, landlordParty, agentParty, repParty))
            .build();

        LandlordDetailsDto result = ApplicationMapper.mapLandlordDetails(ptCaseEntity);

        assertThat(result).isNotNull();
        assertThat(result.getLandlord()).isNotNull();
        assertThat(result.getLandlord().getFirstName()).isEqualTo("Lord");
        assertThat(result.getLandlord().getAddressLine1()).isEqualTo("1 Landlord Way");
        assertThat(result.getLettingAgent()).isNotNull();
        assertThat(result.getLettingAgent().getFirstName()).isEqualTo("Agent");
        assertThat(result.getRepresentative()).isNotNull();
        assertThat(result.getRepresentative().getFirstName()).isEqualTo("Rep");
        assertThat(result.getLandlordRepresentativeType()).isEqualTo(LandlordRepresentativeType.LETTING_AGENT);
    }

    @Test
    void shouldMapLandlordDetailsWhenPartiesEmpty() {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .parties(Collections.emptyList())
            .build();

        LandlordDetailsDto result = ApplicationMapper.mapLandlordDetails(ptCaseEntity);

        assertThat(result).isNotNull();
        assertThat(result.getLandlord()).isNull();
        assertThat(result.getLettingAgent()).isNull();
        assertThat(result.getRepresentative()).isNull();
        assertThat(result.getLandlordRepresentativeType()).isNull();
    }

    @Test
    void shouldMapPartyWhenEntityNull() {
        assertThat(ApplicationMapper.mapParty(null)).isNull();
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
                           .additionalPropertyInfoToConsiderWhenDeterminingRent(YesOrNo.YES)
                           .additionalPropertyInfoToConsiderWhenDeterminingRentDetails("Renovations")
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

    private static List<CasePartyContactPreferenceEntity> contactPreferences(YesOrNo text) {
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
            contactPreferences(YesOrNo.YES),
            partyAddresses
        );
        return entityWithCaseType(caseParty, APPLICATION_TYPE);
    }

    private static ApplicationDto expectedDto(UUID userId) {
        return ApplicationDto.builder()
            .caseReference(CASE_REFERENCE)
            .postcode(POSTCODE)
            .applicationType(APPLICATION_TYPE)
            .tenancyType(TENANCY_TYPE)
            .applicantFirstName(FIRST_NAME)
            .applicantLastName(LAST_NAME)
            .email(EMAIL)
            .applicantIdamUserId(userId)
            .createdDate(CREATED_DATE)
            .applicantContactPreferences(
                ContactPreferencesDto.builder()
                    .phoneNumber(PHONE_NUMBER)
                    .mobilePhoneNumber(MOBILE_NUMBER)
                    .contactByText(YesOrNo.YES)
                    .build())
            .tenantDetails(
                TenantDetailsDto.builder()
                    .firstName(FIRST_NAME)
                    .lastName(LAST_NAME)
                    .companyName(COMPANY_NAME)
                    .referenceNumberForCommunications(REFERENCE_NUMBER)
                    .build())
            .hearingInspectionDetails(
                HearingInspectionDetailsDto.builder()
                    .hearingRequested(YesOrNo.YES)
                    .agreeToDecisionWithoutInspection(YesOrNo.YES)
                    .noDecisionWithoutInspectionReason("Inspection reason")
                    .build())
            .noticeOfRentIncreaseDetails(NoticeOfRentIncreaseDto.builder().build())
            .propertyDetails(
                PropertyDetailsDto.builder()
                    .addressLine1("123 Test St")
                    .postTown("London")
                    .postcode(POSTCODE)
                    .propertyType(PropertyType.TERRACED_HOUSE)
                    .propertyRoomsDocuments(List.of())
                    .build())
            .currentRentsDetails(CurrentRentsDetailsDto.builder().build())
            .marketRentDetails(
                MarketRentDto.builder()
                    .applicantSuggestedMonthlyMarketRent(new BigDecimal("1200.00"))
                    .applicantSuggestedMonthlyMarketRentReasons("Market rate for the area")
                    .additionalPropertyInfoToConsiderWhenDetermining(YesOrNo.YES)
                    .additionalPropertyInfoToConsiderWhenDeterminingDetails("Renovations")
                    .build())
            .tenancyAgreementDetails(
                TenancyAgreementDto.builder()
                    .copyOfTenancyAgreement(YesOrNo.YES)
                    .noTenancyAgreementReason("No agreement reason")
                    .tenancyAgreementDocument(
                        DocumentDto.builder()
                            .url("http://dm-store/doc/tenancy-agreement")
                            .binaryUrl("http://dm-store/doc/tenancy-agreement/binary")
                            .filename("tenancy-agreement.pdf")
                            .contentType("application/pdf")
                            .size(2048L)
                            .build())
                    .build())
            .build();
    }

    private static PropertyDetailsDto expectedPropertyDetailsDto() {
        return PropertyDetailsDto.builder()
            .addressLine1("123 Test Street")
            .addressLine2("Apt 4")
            .postTown("Manchester")
            .county("Greater Manchester")
            .postcode("M1 1AA")
            .propertyType(PropertyType.TERRACED_HOUSE)
            .rentingFlatDetails("Flat details")
            .rentingRoomDetails("Room details")
            .otherMethodRentingDetails("Other details")
            .propertyFloorPlanAvailable(YesOrNo.YES)
            .floorPlanManualDetails("Manual plan details")
            .floorPlanDocument(
                DocumentDto.builder()
                    .url("http://dm-store/doc/floor")
                    .binaryUrl("http://dm-store/doc/floor/binary")
                    .filename("floor.pdf")
                    .contentType("application/pdf")
                    .size(1000L)
                    .build())
            .indoorFeatures("Indoor features")
            .otherFacilitiesAvailable(YesOrNo.YES)
            .otherFacilitiesDetails("Parking and garden")
            .outsidePropertyDocument(
                DocumentDto.builder()
                    .url("http://dm-store/doc/outside")
                    .binaryUrl("http://dm-store/doc/outside/binary")
                    .filename("outside.pdf")
                    .contentType("application/pdf")
                    .size(2000L)
                    .build())
            .propertyRoomsDocuments(List.of(
                DocumentDto.builder()
                    .url("http://dm-store/doc/room1")
                    .binaryUrl("http://dm-store/doc/room1/binary")
                    .filename("room1.pdf")
                    .contentType("application/pdf")
                    .size(3000L)
                    .build(),
                DocumentDto.builder()
                    .url("http://dm-store/doc/room2")
                    .binaryUrl("http://dm-store/doc/room2/binary")
                    .filename("room2.pdf")
                    .contentType("application/pdf")
                    .size(3500L)
                    .build()
            ))
            .furnitureProvidedInTenancy(YesOrNo.YES)
            .furnitureProvidedInTenancyDetails("Bed, table")
            .additionalServicesProvidedInTenancy(YesOrNo.NO)
            .additionalServicesProvidedInTenancyDetails("None")
            .sharePropertyWithLandlord(YesOrNo.NO)
            .sharePropertyWithLandlordDetails("No sharing")
            .landlordRepairsDetails("Roof leak")
            .tenantRepairsDetails("Painted wall")
            .anyTenantsMadePropertyRepairs(YesNoNotSure.YES)
            .repairsEvidenceDocument(
                DocumentDto.builder()
                    .url("http://dm-store/doc/repairs")
                    .binaryUrl("http://dm-store/doc/repairs/binary")
                    .filename("repairs.pdf")
                    .contentType("application/pdf")
                    .size(4000L)
                    .build())
            .build();
    }

    private static CurrentRentsDetailsDto expectedCurrentRentsDetailsDto(
        LocalDateTime startDate,
        LocalDateTime endDate,
        LocalDateTime originalStartDate
    ) {
        return CurrentRentsDetailsDto.builder()
            .tribunalPreviouslyDeterminedTenancyRent(YesOrNo.YES)
            .previousTribunalCaseReference("TRIB-123")
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
            .utilitiesPaidFrequencyAndCostDetails("Utilities details")
            .rentInclusiveOfUtilityCharges(YesOrNo.YES)
            .currentTenancyStartDate(startDate)
            .currentTenancyEndDate(endDate)
            .currentTenancyReplaceOriginalTenancy(YesNoNotSure.YES)
            .originalTenancyStartDate(originalStartDate)
            .additionalRentalServiceChargesVary(YesOrNo.YES)
            .varyingAdditionalRentalServiceChargesDetails("Service charge details")
            .build();
    }
}
