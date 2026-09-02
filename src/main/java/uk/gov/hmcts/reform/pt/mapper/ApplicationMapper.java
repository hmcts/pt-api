package uk.gov.hmcts.reform.pt.mapper;

import uk.gov.hmcts.reform.pt.ccd.domain.DocumentType;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyRole;
import uk.gov.hmcts.reform.pt.dto.ApplicationDto;
import uk.gov.hmcts.reform.pt.dto.ContactPreferencesDto;
import uk.gov.hmcts.reform.pt.dto.CurrentRentsDetailsDto;
import uk.gov.hmcts.reform.pt.dto.DocumentDto;
import uk.gov.hmcts.reform.pt.dto.HearingInspectionDetailsDto;
import uk.gov.hmcts.reform.pt.dto.LandlordDetailsDto;
import uk.gov.hmcts.reform.pt.dto.MarketRentDto;
import uk.gov.hmcts.reform.pt.dto.NoticeOfRentIncreaseDto;
import uk.gov.hmcts.reform.pt.dto.PartyDto;
import uk.gov.hmcts.reform.pt.dto.PropertyDetailsDto;
import uk.gov.hmcts.reform.pt.dto.TenancyAgreementDto;
import uk.gov.hmcts.reform.pt.dto.TenantDetailsDto;
import uk.gov.hmcts.reform.pt.entity.AddressEntity;
import uk.gov.hmcts.reform.pt.entity.CaseApplicationEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyContactPreferenceEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.DocumentEntity;
import uk.gov.hmcts.reform.pt.entity.MarketRentCaseEntity;
import uk.gov.hmcts.reform.pt.entity.NoticeOfRentChangeEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.PropertyInspectionEntity;
import uk.gov.hmcts.reform.pt.entity.TenancyDetailsEntity;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.exception.CasePartyNotFoundException;

import java.util.List;
import java.util.Optional;

public class ApplicationMapper {
    public static ApplicationDto toDto(CaseApplicationEntity entity) {
        CasePartyEntity caseParty = entity.getCaseParty();
        if (caseParty == null) {
            throw new CasePartyNotFoundException("Case party not found for application: " + entity.getId());
        }
        PTCaseEntity ptCase = caseParty.getPtCase();
        if (ptCase == null) {
            throw new CaseNotFoundException("Case not found for application: " + entity.getId());
        }

        return ApplicationDto.builder()
            .caseReference(ptCase.getCaseReference())
            .postcode(
                !ptCase.getAddresses().isEmpty()
                    ? ptCase.getAddresses().getFirst().getPostcode()
                    : "")
            .applicationType(
                entity.getCaseType() != null
                    ? entity.getCaseType().getApplicationTypeName()
                    : null)
            .tenancyType(
                !ptCase.getTenancyDetails().isEmpty()
                    ? ptCase.getTenancyDetails().getFirst().getTenancyType()
                    : null)
            .applicantFirstName(caseParty.getFirstName())
            .applicantLastName(caseParty.getLastName())
            .applicantIdamUserId(
                !caseParty.getAccess().isEmpty()
                    ? caseParty.getAccess().getFirst().getIdamId()
                    : null)
            .email(caseParty.getEmailAddress())
            .applicantContactPreferences(mapContactPreferences(caseParty))
            .tenantDetails(mapTenantDetails(caseParty))
            .hearingInspectionDetails(mapHearingInspectionDetails(ptCase))
            .noticeOfRentIncreaseDetails(mapNoticeOfRentChangeDetails(ptCase))
            .propertyDetails(mapPropertyDetails(ptCase, caseParty))
            .currentRentsDetails(mapCurrentRentDetails(ptCase))
            .marketRentDetails(mapMarketRentDetails(ptCase))
            .tenancyAgreementDetails(mapTenancyAgreement(ptCase))
            .createdDate(entity.getCreatedDate())
            .build();
    }

    public static ContactPreferencesDto mapContactPreferences(CasePartyEntity caseParty) {
        CasePartyContactPreferenceEntity contactPreferences = caseParty.getContactPreferences().stream()
            .findFirst()
            .orElse(null);

        return ContactPreferencesDto.builder()
            .phoneNumber(caseParty.getPhoneNumber())
            .mobilePhoneNumber(caseParty.getMobilePhoneNumber())
            .contactByText(
                contactPreferences != null
                    ? contactPreferences.getContactByText()
                    : null)
            .build();
    }

    public static TenantDetailsDto mapTenantDetails(CasePartyEntity caseParty) {
        return TenantDetailsDto.builder()
            .firstName(caseParty.getFirstName())
            .lastName(caseParty.getLastName())
            .companyName(caseParty.getOrganisationName())
            .referenceNumberForCommunications(caseParty.getReferenceNumber())
            .build();
    }

    public static HearingInspectionDetailsDto mapHearingInspectionDetails(PTCaseEntity ptCaseEntity) {
        PropertyInspectionEntity propertyInspectionEntity = ptCaseEntity.getPropertyInspections().stream()
            .findFirst()
            .orElse(null);

        return HearingInspectionDetailsDto.builder()
            .hearingRequested(ptCaseEntity.getHearingRequested())
            .agreeToDecisionWithoutInspection(
                propertyInspectionEntity != null
                    ? propertyInspectionEntity.getAgreeToDecisionWithoutInspection()
                    : null)
            .noDecisionWithoutInspectionReason(
                propertyInspectionEntity != null
                    ? propertyInspectionEntity.getNoDecisionWithoutInspectionReason()
                    : null)
            .build();
    }

    public static NoticeOfRentIncreaseDto mapNoticeOfRentChangeDetails(PTCaseEntity ptCaseEntity) {
        NoticeOfRentChangeEntity entity = ptCaseEntity.getNoticeOfRentChanges().stream()
            .findFirst()
            .orElse(null);

        if (entity == null) {
            return NoticeOfRentIncreaseDto.builder().build();
        }

        return NoticeOfRentIncreaseDto.builder()
            .receivedLandlordNoticeProposingNewRent(entity.getReceivedLandlordNoticeProposingNewRent())
            .noUploadOfNoticeProposingNewRentReason(entity.getNoUploadOfNoticeProposingNewRentReason())
            .landlordNoticeProposingNewRentDocument(
                findDocumentOfType(DocumentType.NEW_RENT_INCREASE_NOTICE, ptCaseEntity)
                    .map(ApplicationMapper::mapDocument)
                    .orElse(null))
            .noticeLegallyValid(entity.getNoticeLegallyValid())
            .noticeNotLegallyValidDetails(entity.getNoticeNotLegallyValidDetails())
            .noticeNotLegallyValidDocument(
                findDocumentOfType(DocumentType.NOTICE_NOT_LEGALLY_VALID_EVIDENCE, ptCaseEntity)
                    .map(ApplicationMapper::mapDocument)
                    .orElse(null))
            .rentIncreaseToCauseHardship(entity.getRentIncreaseToCauseHardship())
            .rentIncreaseToCauseHardshipDocument(
                findDocumentOfType(DocumentType.HARDSHIP_EVIDENCE, ptCaseEntity)
                    .map(ApplicationMapper::mapDocument)
                    .orElse(null))
            .build();
    }

    public static PropertyDetailsDto mapPropertyDetails(PTCaseEntity ptCaseEntity, CasePartyEntity party) {
        TenancyDetailsEntity tenancyDetails = ptCaseEntity.getTenancyDetails().stream().findFirst().orElse(null);
        MarketRentCaseEntity marketRentCase = ptCaseEntity.getMarketRentCases().stream().findFirst().orElse(null);
        AddressEntity address = party.getAddresses().stream().findFirst().orElse(null);

        if (tenancyDetails == null || marketRentCase == null || address == null) {
            return null;
        }

        return PropertyDetailsDto.builder()
            .addressLine1(address.getAddressLine1())
            .addressLine2(address.getAddressLine2())
            .postTown(address.getPostTown())
            .county(address.getCounty())
            .postcode(address.getPostcode())
            .propertyType(marketRentCase.getTypeOfPropertyRenting())
            .rentingFlatDetails(marketRentCase.getRentingFlatDetails())
            .rentingRoomDetails(marketRentCase.getRentingRoomDetails())
            .otherMethodRentingDetails(marketRentCase.getOtherMethodOfRentDetails())
            .propertyFloorPlanAvailable(marketRentCase.getPropertyFloorPlanAvailable())
            .floorPlanManualDetails(marketRentCase.getFloorplanManualDetails())
            .floorPlanDocument(
                findDocumentOfType(DocumentType.FLOOR_PLAN, ptCaseEntity)
                    .map(ApplicationMapper::mapDocument)
                    .orElse(null))
            .indoorFeatures(marketRentCase.getPropertyIndoorFeatures())
            .otherFacilitiesAvailable(tenancyDetails.getTenancyIncludeFacilities())
            .otherFacilitiesDetails(tenancyDetails.getOtherFacilitiesDetails())
            .outsidePropertyDocument(
                findDocumentOfType(DocumentType.OUTSIDE_PROPERTY, ptCaseEntity)
                    .map(ApplicationMapper::mapDocument)
                    .orElse(null))
            .propertyRoomsDocuments(
                findDocumentsOfType(DocumentType.PROPERTY_ROOMS, ptCaseEntity)
                    .stream()
                    .map(ApplicationMapper::mapDocument)
                    .toList())
            .furnitureProvidedInTenancy(tenancyDetails.getFurnitureProvidedInTenancy())
            .furnitureProvidedInTenancyDetails(tenancyDetails.getFurnitureProvidedInTenancyDetails())
            .additionalServicesProvidedInTenancy(tenancyDetails.getAdditionalServicesProvidedInTenancy())
            .additionalServicesProvidedInTenancyDetails(tenancyDetails.getAdditionalServicesProvidedInTenancyDetails())
            .sharePropertyWithLandlord(marketRentCase.getSharePropertyWithLandlord())
            .sharePropertyWithLandlordDetails(marketRentCase.getSharePropertyWithLandlordDetails())
            .landlordRepairsDetails(tenancyDetails.getLandlordRepairsDetails())
            .tenantRepairsDetails(tenancyDetails.getTenantRepairsDetails())
            .anyTenantsMadePropertyRepairs(tenancyDetails.getAnyTenantsMadePropertyRepairs())
            .repairsEvidenceDocument(
                findDocumentOfType(DocumentType.REPAIRS_EVIDENCE, ptCaseEntity)
                    .map(ApplicationMapper::mapDocument)
                    .orElse(null))
            .build();
    }

    public static CurrentRentsDetailsDto mapCurrentRentDetails(PTCaseEntity ptCaseEntity) {
        TenancyDetailsEntity tenancyDetails = ptCaseEntity.getTenancyDetails().stream().findFirst().orElse(null);
        MarketRentCaseEntity marketRentCase = ptCaseEntity.getMarketRentCases().stream().findFirst().orElse(null);

        if (tenancyDetails == null || marketRentCase == null) {
            return null;
        }

        return CurrentRentsDetailsDto.builder()
            .tribunalPreviouslyDeterminedTenancyRent(tenancyDetails.getTribunalPreviouslyDeterminedTenancyRent())
            .previousTribunalCaseReference(tenancyDetails.getPreviousTribunalCaseReference())
            .rentPaymentFrequency(marketRentCase.getRentPaymentFrequency())
            .rentCostWeekly(marketRentCase.getRentCostWeekly())
            .rentCostFortnightly(marketRentCase.getRentCostFortnightly())
            .rentCostMonthly(marketRentCase.getRentCostMonthly())
            .rentCostYearly(marketRentCase.getRentCostYearly())
            .rentIncludesCouncilTax(marketRentCase.getRentIncludesCouncilTax())
            .councilTaxFrequency(marketRentCase.getCouncilTaxFrequency())
            .councilTaxCostWeekly(marketRentCase.getCouncilTaxCostWeekly())
            .councilTaxCostFortnightly(marketRentCase.getCouncilTaxCostFortnightly())
            .councilTaxCostMonthly(marketRentCase.getCouncilTaxCostMonthly())
            .councilTaxCostYearly(marketRentCase.getCouncilTaxCostYearly())
            .councilTaxFrequencyAndCostDetails(marketRentCase.getCouncilTaxFrequencyAndCostDetails())
            .utilitiesPaidFrequency(marketRentCase.getUtilitiesPaidFrequency())
            .utilitiesCostWeekly(marketRentCase.getUtilitiesCostWeekly())
            .utilitiesCostFortnightly(marketRentCase.getUtilitiesCostFortnightly())
            .utilitiesCostMonthly(marketRentCase.getUtilitiesCostMonthly())
            .utilitiesCostYearly(marketRentCase.getUtilitiesCostYearly())
            .utilitiesPaidFrequencyAndCostDetails(marketRentCase.getUtilitiesFrequencyAndCostDetails())
            .currentTenancyStartDate(tenancyDetails.getCurrentTenancyStartDate())
            .currentTenancyEndDate(tenancyDetails.getTenancyEndDate())
            .currentTenancyReplaceOriginalTenancy(tenancyDetails.getCurrentTenancyReplaceOriginalTenancy())
            .originalTenancyStartDate(tenancyDetails.getOriginalTenancyStartDate())
            .additionalRentalServiceChargesVary(marketRentCase.getAdditionalRentalServiceChargesVary())
            .additionalRentalVaryingServiceChargesDetails(
                marketRentCase.getAdditionalRentalVaryingServiceChargesDetails())
            .anyOtherHouseholdManagementCharges(marketRentCase.getOtherHouseholdManagementCharges())
            .otherHouseholdManagementChargesDetails(marketRentCase.getOtherHouseholdManagementChargesDetails())
            .build();
    }

    public static MarketRentDto mapMarketRentDetails(PTCaseEntity ptCaseEntity) {
        MarketRentCaseEntity marketRentCase = ptCaseEntity.getMarketRentCases().stream().findFirst().orElse(null);
        if (marketRentCase == null) {
            return null;
        }

        return MarketRentDto.builder()
            .applicantSuggestedMonthlyMarketRent(marketRentCase.getApplicantSuggestedMonthlyMarketRent())
            .applicantSuggestedMonthlyMarketRentReasons(marketRentCase.getApplicantSuggestedMonthlyMarketRentReasons())
            .suggestedMarketRentEvidence(
                findDocumentOfType(DocumentType.PROPOSED_RENT_EVIDENCE, ptCaseEntity)
                    .map(ApplicationMapper::mapDocument)
                    .orElse(null))
            .additionalPropertyInfoToConsiderWhenDetermining(
                marketRentCase.getAdditionalPropertyInfoToConsiderWhenDeterminingRent())
            .additionalPropertyInfoToConsiderWhenDeterminingDetails(
                marketRentCase.getAdditionalPropertyInfoToConsiderWhenDeterminingRentDetails())
            .build();
    }

    public static TenancyAgreementDto mapTenancyAgreement(PTCaseEntity ptCaseEntity) {
        TenancyDetailsEntity tenancyDetails = ptCaseEntity.getTenancyDetails().stream().findFirst().orElse(null);
        if (tenancyDetails == null) {
            return null;
        }

        return TenancyAgreementDto.builder()
            .copyOfTenancyAgreement(tenancyDetails.getCopyOfTenancyAgreement())
            .noTenancyAgreementReason(tenancyDetails.getNoTenancyAgreementReason())
            .tenancyAgreementEvidence(
                findDocumentOfType(DocumentType.TENANCY_AGREEMENT, ptCaseEntity)
                    .map(ApplicationMapper::mapDocument)
                    .orElse(null))
            .build();
    }

    public static DocumentDto mapDocument(DocumentEntity entity) {
        return DocumentDto.builder()
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

        AddressEntity address = entity.getAddresses().stream()
            .findFirst()
            .orElse(null);

        return PartyDto.builder()
            .firstName(entity.getFirstName())
            .lastName(entity.getLastName())
            .organisationName(entity.getOrganisationName())
            .emailAddress(entity.getEmailAddress())
            .phoneNumber(entity.getPhoneNumber())
            .dxNumber(entity.getReferenceNumber())
            .addressLine1(address != null ? address.getAddressLine1() : null)
            .addressLine2(address != null ? address.getAddressLine2() : null)
            .postTown(address != null ? address.getPostTown() : null)
            .county(address != null ? address.getCounty() : null)
            .postcode(address != null ? address.getPostcode() : null)
            .build();
    }

    private static Optional<DocumentEntity> findDocumentOfType(DocumentType type, PTCaseEntity ptCaseEntity) {
        return ptCaseEntity.getDocuments().stream()
            .filter(document -> document.getDocumentType() == type)
            .findFirst();
    }

    private static List<DocumentEntity> findDocumentsOfType(DocumentType type, PTCaseEntity ptCaseEntity) {
        return ptCaseEntity.getDocuments().stream()
            .filter(document -> document.getDocumentType() == type)
            .toList();
    }

    private static CasePartyEntity getPartyWithRole(PTCaseEntity ptCaseEntity, PartyRole role) {
        return ptCaseEntity.getParties().stream()
            .filter(party -> party.getRole() != null && party.getRole().getRoleName() == role)
            .findFirst()
            .orElse(null);
    }
}
