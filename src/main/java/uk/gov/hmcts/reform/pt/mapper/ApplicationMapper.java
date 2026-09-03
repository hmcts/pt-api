package uk.gov.hmcts.reform.pt.mapper;

import uk.gov.hmcts.reform.pt.ccd.domain.DocumentType;
import uk.gov.hmcts.reform.pt.dto.ApplicationDto;
import uk.gov.hmcts.reform.pt.dto.ContactPreferencesDto;
import uk.gov.hmcts.reform.pt.dto.DocumentDto;
import uk.gov.hmcts.reform.pt.dto.HearingInspectionDetailsDto;
import uk.gov.hmcts.reform.pt.dto.NoticeOfRentIncreaseDto;
import uk.gov.hmcts.reform.pt.dto.PropertyDetailsDto;
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
import java.util.function.Function;

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

        return NoticeOfRentIncreaseDto.builder()
            .receivedLandlordNoticeProposingNewRent(
                get(entity, NoticeOfRentChangeEntity::getReceivedLandlordNoticeProposingNewRent))
            .noUploadOfNoticeProposingNewRentReason(
                get(entity, NoticeOfRentChangeEntity::getNoUploadOfNoticeProposingNewRentReason))
            .landlordNoticeProposingNewRentDocument(
                findDocumentOfType(DocumentType.NEW_RENT_INCREASE_NOTICE, ptCaseEntity)
                    .map(ApplicationMapper::mapDocument)
                    .orElse(null))
            .noticeLegallyValid(get(entity, NoticeOfRentChangeEntity::getNoticeLegallyValid))
            .noticeNotLegallyValidDetails(
                get(entity, NoticeOfRentChangeEntity::getNoticeNotLegallyValidDetails))
            .noticeNotLegallyValidDocument(
                findDocumentOfType(DocumentType.NOTICE_NOT_LEGALLY_VALID_EVIDENCE, ptCaseEntity)
                    .map(ApplicationMapper::mapDocument)
                    .orElse(null))
            .rentIncreaseToCauseHardship(
                get(entity, NoticeOfRentChangeEntity::getRentIncreaseToCauseHardship))
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

        if (tenancyDetails == null && marketRentCase == null && address == null
            && ptCaseEntity.getDocuments().isEmpty()) {
            return null;
        }

        return PropertyDetailsDto.builder()
            .addressLine1(get(address, AddressEntity::getAddressLine1))
            .addressLine2(get(address, AddressEntity::getAddressLine2))
            .postTown(get(address, AddressEntity::getPostTown))
            .county(get(address, AddressEntity::getCounty))
            .postcode(get(address, AddressEntity::getPostcode))
            .propertyType(get(marketRentCase, MarketRentCaseEntity::getTypeOfPropertyRenting))
            .rentingFlatDetails(get(marketRentCase, MarketRentCaseEntity::getRentingFlatDetails))
            .rentingRoomDetails(get(marketRentCase, MarketRentCaseEntity::getRentingRoomDetails))
            .otherMethodRentingDetails(get(marketRentCase, MarketRentCaseEntity::getOtherMethodOfRentDetails))
            .propertyFloorPlanAvailable(get(marketRentCase, MarketRentCaseEntity::getPropertyFloorPlanAvailable))
            .floorPlanManualDetails(get(marketRentCase, MarketRentCaseEntity::getFloorplanManualDetails))
            .floorPlanDocument(
                findDocumentOfType(DocumentType.FLOOR_PLAN, ptCaseEntity)
                    .map(ApplicationMapper::mapDocument)
                    .orElse(null))
            .indoorFeatures(get(marketRentCase, MarketRentCaseEntity::getPropertyIndoorFeatures))
            .otherFacilitiesAvailable(get(tenancyDetails, TenancyDetailsEntity::getTenancyIncludeFacilities))
            .otherFacilitiesDetails(get(tenancyDetails, TenancyDetailsEntity::getOtherFacilitiesDetails))
            .outsidePropertyDocument(
                findDocumentOfType(DocumentType.OUTSIDE_PROPERTY, ptCaseEntity)
                    .map(ApplicationMapper::mapDocument)
                    .orElse(null))
            .propertyRoomsDocuments(
                findDocumentsOfType(DocumentType.PROPERTY_ROOMS, ptCaseEntity)
                    .stream()
                    .map(ApplicationMapper::mapDocument)
                    .toList())
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
            .repairsEvidenceDocument(
                findDocumentOfType(DocumentType.REPAIRS_EVIDENCE, ptCaseEntity)
                    .map(ApplicationMapper::mapDocument)
                    .orElse(null))
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

    private static <T, R> R get(T source, Function<T, R> getter) {
        return source == null ? null : getter.apply(source);
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
}
