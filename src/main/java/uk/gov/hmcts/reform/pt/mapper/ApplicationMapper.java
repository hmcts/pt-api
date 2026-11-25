package uk.gov.hmcts.reform.pt.mapper;

import lombok.experimental.UtilityClass;
import uk.gov.hmcts.reform.pt.ccd.domain.DocumentType;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyRole;
import uk.gov.hmcts.reform.pt.dto.ApplicationDto;
import uk.gov.hmcts.reform.pt.dto.EnrichedApplicationDto;
import uk.gov.hmcts.reform.pt.dto.ContactPreferencesDto;
import uk.gov.hmcts.reform.pt.dto.RentDetailsDto;
import uk.gov.hmcts.reform.pt.dto.DocumentDto;
import uk.gov.hmcts.reform.pt.dto.ExtraSupportDto;
import uk.gov.hmcts.reform.pt.dto.HearingInspectionDetailsDto;
import uk.gov.hmcts.reform.pt.dto.LandlordDetailsDto;
import uk.gov.hmcts.reform.pt.dto.LandlordResponseDto;
import uk.gov.hmcts.reform.pt.dto.NoticeOfRentIncreaseDto;
import uk.gov.hmcts.reform.pt.dto.PartyDto;
import uk.gov.hmcts.reform.pt.dto.PropertyDetailsDto;
import uk.gov.hmcts.reform.pt.dto.TenancyAgreementDto;
import uk.gov.hmcts.reform.pt.dto.TenantDetailsDto;
import uk.gov.hmcts.reform.pt.entity.CasePartyAddressEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.CasePropertyAddressEntity;
import uk.gov.hmcts.reform.pt.entity.CasePropertyDetailsEntity;
import uk.gov.hmcts.reform.pt.entity.DocumentEntity;
import uk.gov.hmcts.reform.pt.entity.NoticeOfRentChangeEntity;
import uk.gov.hmcts.reform.pt.entity.RespondentPropertyDetailsEntity;
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
            .applicationType(ptCase.getCaseSubType())
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
            .extraSupportDetails(mapExtraSupportDetails(caseParty))
            .landlordResponse(mapLandlordResponse(ptCase))
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
            .pcqId(caseParty.getPcqId())
            .build();
    }

    public static HearingInspectionDetailsDto mapHearingInspectionDetails(PTCaseEntity ptCaseEntity) {
        CasePropertyDetailsEntity casePropertyDetails = ptCaseEntity.getCasePropertyDetails();

        return HearingInspectionDetailsDto.builder()
            .hearingRequested(get(casePropertyDetails, CasePropertyDetailsEntity::getHearingRequested))
            .reasonHearingRequested(get(casePropertyDetails, CasePropertyDetailsEntity::getReasonHearingRequested))
            .agreeToDecisionWithoutInspection(
                get(casePropertyDetails, CasePropertyDetailsEntity::getAgreeToDecisionWithoutInspection))
            .noDecisionWithoutInspectionReason(
                get(casePropertyDetails, CasePropertyDetailsEntity::getNoDecisionWithoutInspectionReason))
            .build();
    }

    public static NoticeOfRentIncreaseDto mapNoticeOfRentChangeDetails(PTCaseEntity ptCaseEntity) {
        NoticeOfRentChangeEntity entity =
            get(ptCaseEntity.getCasePropertyDetails(), CasePropertyDetailsEntity::getNoticeOfRentChange);

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
            .noticeNotLegallyValidDocuments(
                findDocumentsOfTypeAndMap(DocumentType.NOTICE_NOT_LEGALLY_VALID_EVIDENCE, ptCaseEntity))
            .rentIncreaseToCauseHardship(
                get(entity, NoticeOfRentChangeEntity::getRentIncreaseToCauseHardship))
            .rentIncreaseToCauseHardshipDetails(
                get(entity, NoticeOfRentChangeEntity::getRentIncreaseToCauseHardshipDetails))
            .rentIncreaseHardshipDocuments(
                findDocumentsOfTypeAndMap(DocumentType.HARDSHIP_EVIDENCE, ptCaseEntity))
            .build();
    }

    public static PropertyDetailsDto mapPropertyDetails(PTCaseEntity ptCaseEntity) {
        TenancyDetailsEntity tenancyDetails = tenancyDetailsOf(ptCaseEntity);
        CasePropertyDetailsEntity casePropertyDetails = ptCaseEntity.getCasePropertyDetails();
        CasePropertyAddressEntity address = ptCaseEntity.getPropertyAddress();

        if (tenancyDetails == null && casePropertyDetails == null && address == null
            && ptCaseEntity.getDocuments().isEmpty()) {
            return null;
        }

        return PropertyDetailsDto.builder()
            .addressLine1(get(address, CasePropertyAddressEntity::getAddressLine1))
            .addressLine2(get(address, CasePropertyAddressEntity::getAddressLine2))
            .postTown(get(address, CasePropertyAddressEntity::getTownCity))
            .county(get(address, CasePropertyAddressEntity::getCounty))
            .postcode(get(address, CasePropertyAddressEntity::getPostcode))
            .propertyType(get(casePropertyDetails, CasePropertyDetailsEntity::getTypeOfPropertyRenting))
            .rentingFlatDetails(get(casePropertyDetails, CasePropertyDetailsEntity::getRentingFlatDetails))
            .rentingRoomDetails(get(casePropertyDetails, CasePropertyDetailsEntity::getRentingRoomDetails))
            .otherMethodRentingDetails(get(casePropertyDetails,
                                           CasePropertyDetailsEntity::getOtherMethodOfRentDetails))
            .propertyFloorPlanAvailable(get(casePropertyDetails,
                                            CasePropertyDetailsEntity::getPropertyFloorPlanAvailable))
            .floorPlanManualDetails(get(casePropertyDetails, CasePropertyDetailsEntity::getFloorplanManualDetails))
            .floorPlanDocuments(
                findDocumentsOfTypeAndMap(DocumentType.PROPERTY_FLOOR_PLAN, ptCaseEntity))
            .indoorFeatures(get(casePropertyDetails, CasePropertyDetailsEntity::getPropertyIndoorFeatures))
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
            .sharePropertyWithLandlord(get(casePropertyDetails,
                                           CasePropertyDetailsEntity::getSharePropertyWithLandlord))
            .sharePropertyWithLandlordDetails(
                get(casePropertyDetails, CasePropertyDetailsEntity::getSharePropertyWithLandlordDetails))
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
        CasePropertyDetailsEntity casePropertyDetails = ptCaseEntity.getCasePropertyDetails();

        if (tenancyDetails == null && casePropertyDetails == null && ptCaseEntity.getDocuments().isEmpty()) {
            return null;
        }

        return RentDetailsDto.builder()
            .tribunalPreviouslyDeterminedTenancyRent(
                get(tenancyDetails, TenancyDetailsEntity::getTribunalPreviouslyDeterminedTenancyRent))
            .previousTribunalCaseReference(
                get(tenancyDetails, TenancyDetailsEntity::getPreviousTribunalCaseReference))
            .rentPaymentFrequency(get(casePropertyDetails, CasePropertyDetailsEntity::getRentPaymentFrequency))
            .rentCostWeekly(get(casePropertyDetails, CasePropertyDetailsEntity::getRentCostWeekly))
            .rentCostFortnightly(get(casePropertyDetails, CasePropertyDetailsEntity::getRentCostFortnightly))
            .rentCostMonthly(get(casePropertyDetails, CasePropertyDetailsEntity::getRentCostMonthly))
            .rentCostYearly(get(casePropertyDetails, CasePropertyDetailsEntity::getRentCostYearly))
            .rentIncludesCouncilTax(get(casePropertyDetails, CasePropertyDetailsEntity::getRentIncludesCouncilTax))
            .councilTaxFrequency(get(casePropertyDetails, CasePropertyDetailsEntity::getCouncilTaxFrequency))
            .councilTaxCostWeekly(get(casePropertyDetails, CasePropertyDetailsEntity::getCouncilTaxCostWeekly))
            .councilTaxCostFortnightly(get(casePropertyDetails,
                                           CasePropertyDetailsEntity::getCouncilTaxCostFortnightly))
            .councilTaxCostMonthly(get(casePropertyDetails, CasePropertyDetailsEntity::getCouncilTaxCostMonthly))
            .councilTaxCostYearly(get(casePropertyDetails, CasePropertyDetailsEntity::getCouncilTaxCostYearly))
            .councilTaxFrequencyAndCostDetails(
                get(casePropertyDetails, CasePropertyDetailsEntity::getCouncilTaxFrequencyAndCostDetails))
            .utilitiesPaidFrequency(get(casePropertyDetails, CasePropertyDetailsEntity::getUtilitiesPaidFrequency))
            .utilitiesCostWeekly(get(casePropertyDetails, CasePropertyDetailsEntity::getUtilitiesCostWeekly))
            .utilitiesCostFortnightly(get(casePropertyDetails, CasePropertyDetailsEntity::getUtilitiesCostFortnightly))
            .utilitiesCostMonthly(get(casePropertyDetails, CasePropertyDetailsEntity::getUtilitiesCostMonthly))
            .utilitiesCostYearly(get(casePropertyDetails, CasePropertyDetailsEntity::getUtilitiesCostYearly))
            .utilitiesPaidFrequencyAndCostDetails(
                get(casePropertyDetails, CasePropertyDetailsEntity::getUtilitiesFrequencyAndCostDetails))
            .rentInclusiveOfUtilityCharges(get(casePropertyDetails,
                                               CasePropertyDetailsEntity::getRentInclusiveOfUtilityCharges))
            .currentTenancyStartDate(get(tenancyDetails, TenancyDetailsEntity::getCurrentTenancyStartDate))
            .currentTenancyEndDate(get(tenancyDetails, TenancyDetailsEntity::getTenancyEndDate))
            .currentTenancyReplaceOriginalTenancy(
                get(tenancyDetails, TenancyDetailsEntity::getCurrentTenancyReplaceOriginalTenancy))
            .originalTenancyStartDate(get(tenancyDetails, TenancyDetailsEntity::getOriginalTenancyStartDate))
            .additionalRentalServiceChargesVary(
                get(casePropertyDetails, CasePropertyDetailsEntity::getAdditionalRentalServiceChargesVary))
            .varyingAdditionalRentalServiceChargesDetails(
                get(casePropertyDetails, CasePropertyDetailsEntity::getVaryingAdditionalRentalServiceChargesDetails))
            .anyOtherHouseholdManagementCharges(
                get(casePropertyDetails, CasePropertyDetailsEntity::getOtherHouseholdManagementCharges))
            .otherHouseholdManagementChargesDetails(
                get(casePropertyDetails, CasePropertyDetailsEntity::getOtherHouseholdManagementChargesDetails))
            .applicantSuggestedMarketRent(
                get(casePropertyDetails, CasePropertyDetailsEntity::getApplicantSuggestedMarketRent))
            .applicantSuggestedMarketRentFrequency(
                get(casePropertyDetails, CasePropertyDetailsEntity::getApplicantSuggestedMarketRentFrequency))
            .applicantSuggestedMarketRentFrequencyOther(
                get(casePropertyDetails, CasePropertyDetailsEntity::getApplicantSuggestedMarketRentFrequencyOther))
            .applicantSuggestedMarketRentReasons(
                get(casePropertyDetails, CasePropertyDetailsEntity::getApplicantSuggestedMarketRentReasons))
            .suggestedMarketRentEvidence(
                findDocumentsOfTypeAndMap(DocumentType.TENANT_PROPOSED_MARKET_RENT_EVIDENCE, ptCaseEntity))
            .additionalPropertyInfoToConsiderWhenDetermining(
                get(casePropertyDetails,
                    CasePropertyDetailsEntity::getAdditionalPropertyInfoToConsiderWhenDeterminingRent))
            .additionalPropertyInfoToConsiderWhenDeterminingDetails(
                get(casePropertyDetails,
                    CasePropertyDetailsEntity::getAdditionalPropertyInfoToConsiderWhenDeterminingRentDetails))
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

    public static LandlordResponseDto mapLandlordResponse(PTCaseEntity ptCaseEntity) {
        RespondentPropertyDetailsEntity respondentPropertyDetails = respondentPropertyDetailsOf(ptCaseEntity);
        CasePartyEntity respondent = getPartyWithRole(ptCaseEntity, PartyRole.LANDLORD);
        List<DocumentDto> tenancyDetailsResponseEvidence =
            findDocumentsOfTypeAndMap(DocumentType.EVIDENCE_SUPPORTING_RESPONSE_TO_TENANCY_DETAILS, ptCaseEntity);
        List<DocumentDto> propertyDetailsResponseEvidence =
            findDocumentsOfTypeAndMap(DocumentType.EVIDENCE_SUPPORTING_RESPONSE_TO_PROPERTY_DETAILS, ptCaseEntity);
        if (respondentPropertyDetails == null && respondent == null && tenancyDetailsResponseEvidence.isEmpty()
            && propertyDetailsResponseEvidence.isEmpty()) {
            return null;
        }

        return LandlordResponseDto.builder()
            .respondToTenantAnswersAboutTenancyDetails(
                get(
                    respondentPropertyDetails,
                    RespondentPropertyDetailsEntity::getRespondToTenantAnswersAboutTenancyDetails
                ))
            .respondToTenantAnswersAboutTenancyDetailsDescription(
                get(
                    respondentPropertyDetails,
                    RespondentPropertyDetailsEntity::getRespondToTenantAnswersAboutTenancyDetailsDescription
                ))
            .respondToTenantAnswersAboutPropertyDetails(
                get(
                    respondentPropertyDetails,
                    RespondentPropertyDetailsEntity::getRespondToTenantAnswersAboutPropertyDetails
                ))
            .respondToTenantAnswersAboutPropertyDetailsDescription(
                get(
                    respondentPropertyDetails,
                    RespondentPropertyDetailsEntity::getRespondToTenantAnswersAboutPropertyDetailsDescription
                ))
            .landlordPhoneNumber(get(respondent, CasePartyEntity::getPhoneNumber))
            .tenancyDetailsResponseEvidence(tenancyDetailsResponseEvidence)
            .propertyDetailsResponseEvidence(propertyDetailsResponseEvidence)
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

    public static ExtraSupportDto mapExtraSupportDetails(CasePartyEntity caseParty) {
        return ExtraSupportDto.builder()
            .extraSupportRequired(caseParty.getExtraSupportRequired())
            .extraSupportRequiredDetails(caseParty.getExtraSupportRequiredDetails())
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
            .pcqId(entity.getPcqId())
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
        return get(ptCaseEntity.getCasePropertyDetails(), CasePropertyDetailsEntity::getTenancyDetails);
    }

    private static RespondentPropertyDetailsEntity respondentPropertyDetailsOf(PTCaseEntity ptCaseEntity) {
        return get(ptCaseEntity.getCasePropertyDetails(), CasePropertyDetailsEntity::getRespondentPropertyDetails);
    }

    private static <T, R> R get(T source, Function<T, R> getter) {
        return source == null ? null : getter.apply(source);
    }
}
