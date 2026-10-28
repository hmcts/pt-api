package uk.gov.hmcts.reform.pt.mapper;

import lombok.experimental.UtilityClass;
import uk.gov.hmcts.reform.pt.ccd.domain.DocumentType;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyRole;
import uk.gov.hmcts.reform.pt.dto.ApplicationDto;
import uk.gov.hmcts.reform.pt.dto.EnrichedApplicationDto;
import uk.gov.hmcts.reform.pt.dto.ContactPreferencesDto;
import uk.gov.hmcts.reform.pt.dto.RentDetailsDto;
import uk.gov.hmcts.reform.pt.dto.DocumentDto;
import uk.gov.hmcts.reform.pt.dto.HearingInspectionDetailsDto;
import uk.gov.hmcts.reform.pt.dto.LandlordDetailsDto;
import uk.gov.hmcts.reform.pt.dto.NoticeOfRentIncreaseDto;
import uk.gov.hmcts.reform.pt.dto.PartyDto;
import uk.gov.hmcts.reform.pt.dto.PropertyDetailsDto;
import uk.gov.hmcts.reform.pt.dto.TenancyAgreementDto;
import uk.gov.hmcts.reform.pt.dto.TenantDetailsDto;
import uk.gov.hmcts.reform.pt.entity.CasePartyAddressEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.CasePropertyAddressEntity;
import uk.gov.hmcts.reform.pt.entity.DocumentEntity;
import uk.gov.hmcts.reform.pt.entity.MarketRentCaseEntity;
import uk.gov.hmcts.reform.pt.entity.NoticeOfRentChangeEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.projection.ApplicationSummary;
import uk.gov.hmcts.reform.pt.entity.TenancyDetailsEntity;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;

import java.util.List;
import java.util.function.Function;

@UtilityClass
public class ApplicationMapper {
    public static ApplicationDto toDto(ApplicationSummary summary) {
        return ApplicationDto.builder()
            .caseReference(summary.getCaseReference())
            .createdDate(summary.getCreatedDate())
            .submittedOn(summary.getSubmittedDate())
            .state(summary.getState())
            .build();
    }

    public static EnrichedApplicationDto toEnrichedDto(CasePartyEntity caseParty) {
        PTCaseEntity ptCase = caseParty.getPtCase();
        if (ptCase == null) {
            throw new CaseNotFoundException("Case not found for case party: " + caseParty.getId());
        }

        return EnrichedApplicationDto.builder()
            .caseReference(ptCase.getCaseReference())
            .postcode(
                ptCase.getPropertyAddress() != null
                    ? ptCase.getPropertyAddress().getPostcode()
                    : "")
            .applicationType(get(ptCase.getMarketRentCase(), MarketRentCaseEntity::getMarketRentCaseType))
            .tenancyType(get(tenancyDetailsOf(ptCase), TenancyDetailsEntity::getTenancyType))
            .applicantFirstName(caseParty.getFirstName())
            .applicantLastName(caseParty.getLastName())
            .applicantIdamUserId(caseParty.getIdamId())
            .email(caseParty.getEmailAddress())
            .applicantContactPreferences(mapContactPreferences(caseParty))
            .tenantDetails(mapTenantDetails(caseParty))
            .hearingInspectionDetails(mapHearingInspectionDetails(ptCase))
            .noticeOfRentIncreaseDetails(mapNoticeOfRentChangeDetails(ptCase))
            .propertyDetails(mapPropertyDetails(ptCase))
            .rentDetails(mapRentDetails(ptCase))
            .tenancyAgreementDetails(mapTenancyAgreement(ptCase))
            .createdDate(ptCase.getCreatedDate())
            .build();
    }

    public static ContactPreferencesDto mapContactPreferences(CasePartyEntity caseParty) {
        return ContactPreferencesDto.builder()
            .phoneNumber(caseParty.getPhoneNumber())
            .mobilePhoneNumber(caseParty.getMobilePhoneNumber())
            .contactByText(caseParty.getContactByText())
            .build();
    }

    public static TenantDetailsDto mapTenantDetails(CasePartyEntity caseParty) {
        return TenantDetailsDto.builder()
            .firstName(caseParty.getFirstName())
            .lastName(caseParty.getLastName())
            .companyName(caseParty.getOrganisationName())
            .referenceNumberForCommunications(caseParty.getReferenceNumberForCommunications())
            .build();
    }

    public static HearingInspectionDetailsDto mapHearingInspectionDetails(PTCaseEntity ptCaseEntity) {
        MarketRentCaseEntity marketRentCase = ptCaseEntity.getMarketRentCase();

        return HearingInspectionDetailsDto.builder()
            .hearingRequested(get(marketRentCase, MarketRentCaseEntity::getHearingRequested))
            .reasonHearingRequested(get(marketRentCase, MarketRentCaseEntity::getReasonHearingRequested))
            .agreeToDecisionWithoutInspection(
                get(marketRentCase, MarketRentCaseEntity::getAgreeToDecisionWithoutInspection))
            .noDecisionWithoutInspectionReason(
                get(marketRentCase, MarketRentCaseEntity::getNoDecisionWithoutInspectionReason))
            .build();
    }

    public static NoticeOfRentIncreaseDto mapNoticeOfRentChangeDetails(PTCaseEntity ptCaseEntity) {
        NoticeOfRentChangeEntity entity =
            get(ptCaseEntity.getMarketRentCase(), MarketRentCaseEntity::getNoticeOfRentChange);

        return NoticeOfRentIncreaseDto.builder()
            .receivedLandlordNoticeProposingNewRent(
                get(entity, NoticeOfRentChangeEntity::getReceivedLandlordNoticeProposingNewRent))
            .noUploadOfNoticeProposingNewRentReason(
                get(entity, NoticeOfRentChangeEntity::getNoUploadOfNoticeProposingNewRentReason))
            .landlordNoticeProposingNewRentDocument(
                findDocumentOfTypeAndMap(DocumentType.NEW_RENT_INCREASE_NOTICE, ptCaseEntity))
            .noticeLegallyValid(get(entity, NoticeOfRentChangeEntity::getNoticeLegallyValid))
            .noticeNotLegallyValidDetails(
                get(entity, NoticeOfRentChangeEntity::getNoticeNotLegallyValidDetails))
            .noticeNotLegallyValidDocument(
                findDocumentOfTypeAndMap(DocumentType.NOTICE_NOT_LEGALLY_VALID_EVIDENCE, ptCaseEntity))
            .rentIncreaseToCauseHardship(
                get(entity, NoticeOfRentChangeEntity::getRentIncreaseToCauseHardship))
            .rentIncreaseToCauseHardshipDocument(
                findDocumentOfTypeAndMap(DocumentType.HARDSHIP_EVIDENCE, ptCaseEntity))
            .build();
    }

    public static PropertyDetailsDto mapPropertyDetails(PTCaseEntity ptCaseEntity) {
        TenancyDetailsEntity tenancyDetails = tenancyDetailsOf(ptCaseEntity);
        MarketRentCaseEntity marketRentCase = ptCaseEntity.getMarketRentCase();
        CasePropertyAddressEntity address = ptCaseEntity.getPropertyAddress();

        if (tenancyDetails == null && marketRentCase == null && address == null
            && ptCaseEntity.getDocuments().isEmpty()) {
            return null;
        }

        return PropertyDetailsDto.builder()
            .addressLine1(get(address, CasePropertyAddressEntity::getAddressLine1))
            .addressLine2(get(address, CasePropertyAddressEntity::getAddressLine2))
            .postTown(get(address, CasePropertyAddressEntity::getTownCity))
            .county(get(address, CasePropertyAddressEntity::getCounty))
            .postcode(get(address, CasePropertyAddressEntity::getPostcode))
            .propertyType(get(marketRentCase, MarketRentCaseEntity::getTypeOfPropertyRenting))
            .rentingFlatDetails(get(marketRentCase, MarketRentCaseEntity::getRentingFlatDetails))
            .rentingRoomDetails(get(marketRentCase, MarketRentCaseEntity::getRentingRoomDetails))
            .otherMethodRentingDetails(get(marketRentCase, MarketRentCaseEntity::getOtherMethodOfRentDetails))
            .propertyFloorPlanAvailable(get(marketRentCase, MarketRentCaseEntity::getPropertyFloorPlanAvailable))
            .floorPlanManualDetails(get(marketRentCase, MarketRentCaseEntity::getFloorplanManualDetails))
            .floorPlanDocuments(
                findDocumentsOfTypeAndMap(DocumentType.PROPERTY_FLOOR_PLAN, ptCaseEntity))
            .indoorFeatures(get(marketRentCase, MarketRentCaseEntity::getPropertyIndoorFeatures))
            .otherFacilitiesAvailable(get(tenancyDetails, TenancyDetailsEntity::getTenancyIncludeFacilities))
            .otherFacilitiesDetails(get(tenancyDetails, TenancyDetailsEntity::getOtherFacilitiesDetails))
            .outsidePropertyDocuments(
                findDocumentsOfTypeAndMap(DocumentType.OUTSIDE_PROPERTY, ptCaseEntity))
            .propertyRoomsDocuments(
                findDocumentsOfTypeAndMap(DocumentType.PROPERTY_ROOMS, ptCaseEntity))
            .furnitureProvidedInTenancy(
                get(tenancyDetails, TenancyDetailsEntity::getFurnitureProvidedInTenancy))
            .furnitureProvidedInTenancyDetails(
                get(tenancyDetails, TenancyDetailsEntity::getFurnitureProvidedInTenancyDetails))
            .additionalServicesProvidedInTenancy(
                get(tenancyDetails, TenancyDetailsEntity::getAdditionalServicesProvidedInTenancy))
            .additionalServicesProvidedInTenancyDetails(
                get(tenancyDetails, TenancyDetailsEntity::getAdditionalServicesProvidedInTenancyDetails))
            .sharePropertyWithLandlord(get(marketRentCase, MarketRentCaseEntity::getSharePropertyWithLandlord))
            .sharePropertyWithLandlordDetails(
                get(marketRentCase, MarketRentCaseEntity::getSharePropertyWithLandlordDetails))
            .landlordRepairsDetails(get(tenancyDetails, TenancyDetailsEntity::getLandlordRepairsDetails))
            .tenantRepairsDetails(get(tenancyDetails, TenancyDetailsEntity::getTenantRepairsDetails))
            .anyTenantsMadePropertyRepairs(
                get(tenancyDetails, TenancyDetailsEntity::getAnyTenantsMadePropertyRepairs))
            .repairsEvidenceDocuments(
                findDocumentsOfTypeAndMap(DocumentType.TENANT_REPAIRS_EVIDENCE, ptCaseEntity))
            .build();
    }

    public static RentDetailsDto mapRentDetails(PTCaseEntity ptCaseEntity) {
        TenancyDetailsEntity tenancyDetails = tenancyDetailsOf(ptCaseEntity);
        MarketRentCaseEntity marketRentCase = ptCaseEntity.getMarketRentCase();

        if (tenancyDetails == null && marketRentCase == null && ptCaseEntity.getDocuments().isEmpty()) {
            return null;
        }

        return RentDetailsDto.builder()
            .tribunalPreviouslyDeterminedTenancyRent(
                get(tenancyDetails, TenancyDetailsEntity::getTribunalPreviouslyDeterminedTenancyRent))
            .previousTribunalCaseReference(
                get(tenancyDetails, TenancyDetailsEntity::getPreviousTribunalCaseReference))
            .rentPaymentFrequency(get(marketRentCase, MarketRentCaseEntity::getRentPaymentFrequency))
            .rentCostWeekly(get(marketRentCase, MarketRentCaseEntity::getRentCostWeekly))
            .rentCostFortnightly(get(marketRentCase, MarketRentCaseEntity::getRentCostFortnightly))
            .rentCostMonthly(get(marketRentCase, MarketRentCaseEntity::getRentCostMonthly))
            .rentCostYearly(get(marketRentCase, MarketRentCaseEntity::getRentCostYearly))
            .rentIncludesCouncilTax(get(marketRentCase, MarketRentCaseEntity::getRentIncludesCouncilTax))
            .councilTaxFrequency(get(marketRentCase, MarketRentCaseEntity::getCouncilTaxFrequency))
            .councilTaxCostWeekly(get(marketRentCase, MarketRentCaseEntity::getCouncilTaxCostWeekly))
            .councilTaxCostFortnightly(get(marketRentCase, MarketRentCaseEntity::getCouncilTaxCostFortnightly))
            .councilTaxCostMonthly(get(marketRentCase, MarketRentCaseEntity::getCouncilTaxCostMonthly))
            .councilTaxCostYearly(get(marketRentCase, MarketRentCaseEntity::getCouncilTaxCostYearly))
            .councilTaxFrequencyAndCostDetails(
                get(marketRentCase, MarketRentCaseEntity::getCouncilTaxFrequencyAndCostDetails))
            .utilitiesPaidFrequency(get(marketRentCase, MarketRentCaseEntity::getUtilitiesPaidFrequency))
            .utilitiesCostWeekly(get(marketRentCase, MarketRentCaseEntity::getUtilitiesCostWeekly))
            .utilitiesCostFortnightly(get(marketRentCase, MarketRentCaseEntity::getUtilitiesCostFortnightly))
            .utilitiesCostMonthly(get(marketRentCase, MarketRentCaseEntity::getUtilitiesCostMonthly))
            .utilitiesCostYearly(get(marketRentCase, MarketRentCaseEntity::getUtilitiesCostYearly))
            .utilitiesPaidFrequencyAndCostDetails(
                get(marketRentCase, MarketRentCaseEntity::getUtilitiesFrequencyAndCostDetails))
            .rentInclusiveOfUtilityCharges(get(marketRentCase, MarketRentCaseEntity::getRentInclusiveOfUtilityCharges))
            .currentTenancyStartDate(get(tenancyDetails, TenancyDetailsEntity::getCurrentTenancyStartDate))
            .currentTenancyEndDate(get(tenancyDetails, TenancyDetailsEntity::getTenancyEndDate))
            .currentTenancyReplaceOriginalTenancy(
                get(tenancyDetails, TenancyDetailsEntity::getCurrentTenancyReplaceOriginalTenancy))
            .originalTenancyStartDate(get(tenancyDetails, TenancyDetailsEntity::getOriginalTenancyStartDate))
            .additionalRentalServiceChargesVary(
                get(marketRentCase, MarketRentCaseEntity::getAdditionalRentalServiceChargesVary))
            .varyingAdditionalRentalServiceChargesDetails(
                get(marketRentCase, MarketRentCaseEntity::getVaryingAdditionalRentalServiceChargesDetails))
            .anyOtherHouseholdManagementCharges(
                get(marketRentCase, MarketRentCaseEntity::getOtherHouseholdManagementCharges))
            .otherHouseholdManagementChargesDetails(
                get(marketRentCase, MarketRentCaseEntity::getOtherHouseholdManagementChargesDetails))
            .applicantSuggestedMarketRent(
                get(marketRentCase, MarketRentCaseEntity::getApplicantSuggestedMarketRent))
            .applicantSuggestedMarketRentReasons(
                get(marketRentCase, MarketRentCaseEntity::getApplicantSuggestedMarketRentReasons))
            .suggestedMarketRentEvidence(
                findDocumentsOfTypeAndMap(DocumentType.TENANT_PROPOSED_MARKET_RENT_EVIDENCE, ptCaseEntity))
            .additionalPropertyInfoToConsiderWhenDetermining(
                get(marketRentCase, MarketRentCaseEntity::getAdditionalPropertyInfoToConsiderWhenDeterminingRent))
            .additionalPropertyInfoToConsiderWhenDeterminingDetails(
                get(marketRentCase,
                    MarketRentCaseEntity::getAdditionalPropertyInfoToConsiderWhenDeterminingRentDetails))
            .build();
    }

    public static TenancyAgreementDto mapTenancyAgreement(PTCaseEntity ptCaseEntity) {
        TenancyDetailsEntity tenancyDetails = tenancyDetailsOf(ptCaseEntity);
        if (tenancyDetails == null && ptCaseEntity.getDocuments().isEmpty()) {
            return null;
        }

        return TenancyAgreementDto.builder()
            .copyOfTenancyAgreement(get(tenancyDetails, TenancyDetailsEntity::getCopyOfTenancyAgreement))
            .noTenancyAgreementReason(get(tenancyDetails, TenancyDetailsEntity::getNoTenancyAgreementReason))
            .tenancyAgreementDocument(
                findDocumentOfTypeAndMap(DocumentType.TENANCY_AGREEMENT, ptCaseEntity))
            .build();
    }

    public static DocumentDto mapDocument(DocumentEntity entity) {
        return DocumentDto.builder()
            .id(entity.getId())
            .url(entity.getUrl())
            .binaryUrl(entity.getBinaryUrl())
            .filename(entity.getFileName())
            .contentType(entity.getContentType())
            .size(entity.getSize())
            .build();
    }

    public static LandlordDetailsDto mapLandlordDetails(PTCaseEntity ptCaseEntity) {
        return LandlordDetailsDto.builder()
            .landlord(mapParty(getPartyWithRole(ptCaseEntity, PartyRole.LANDLORD)))
            .lettingAgent(mapParty(getPartyWithRole(ptCaseEntity, PartyRole.LETTING_AGENT)))
            .representative(mapParty(getPartyWithRole(ptCaseEntity, PartyRole.LANDLORD_REPRESENTATIVE)))
            .landlordRepresentativeType(ptCaseEntity.getLandlordType())
            .build();

    }

    public static PartyDto mapParty(CasePartyEntity entity) {
        if (entity == null) {
            return null;
        }

        CasePartyAddressEntity address = entity.getAddress();

        return PartyDto.builder()
            .firstName(entity.getFirstName())
            .lastName(entity.getLastName())
            .organisationName(entity.getOrganisationName())
            .emailAddress(entity.getEmailAddress())
            .phoneNumber(entity.getPhoneNumber())
            .dxNumber(entity.getReferenceNumberForCommunications())
            .addressLine1(get(address, CasePartyAddressEntity::getAddressLine1))
            .addressLine2(get(address, CasePartyAddressEntity::getAddressLine2))
            .postTown(get(address, CasePartyAddressEntity::getTownCity))
            .county(get(address, CasePartyAddressEntity::getCounty))
            .postcode(get(address, CasePartyAddressEntity::getPostcode))
            .build();
    }

    public static CasePartyEntity getPartyWithRole(PTCaseEntity ptCaseEntity, PartyRole role) {
        return ptCaseEntity.getParties().stream()
            .filter(party -> party.getCasePartyRole() == role)
            .findFirst()
            .orElse(null);
    }

    private static DocumentDto findDocumentOfTypeAndMap(DocumentType type, PTCaseEntity ptCaseEntity) {
        return ptCaseEntity.getDocuments().stream()
            .filter(document -> document.getDocumentType() == type)
            .findFirst()
            .map(ApplicationMapper::mapDocument)
            .orElse(null);
    }

    private static List<DocumentDto> findDocumentsOfTypeAndMap(DocumentType type, PTCaseEntity ptCaseEntity) {
        return ptCaseEntity.getDocuments().stream()
            .filter(document -> document.getDocumentType() == type)
            .map(ApplicationMapper::mapDocument)
            .toList();
    }

    private static TenancyDetailsEntity tenancyDetailsOf(PTCaseEntity ptCaseEntity) {
        return get(ptCaseEntity.getMarketRentCase(), MarketRentCaseEntity::getTenancyDetails);
    }

    private static <T, R> R get(T source, Function<T, R> getter) {
        return source == null ? null : getter.apply(source);
    }

}
