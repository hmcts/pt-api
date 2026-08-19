package uk.gov.hmcts.reform.pt.mapper;

import uk.gov.hmcts.reform.pt.ccd.domain.DocumentType;
import uk.gov.hmcts.reform.pt.dto.ApplicationDto;
import uk.gov.hmcts.reform.pt.dto.ContactPreferencesDto;
import uk.gov.hmcts.reform.pt.dto.DocumentDto;
import uk.gov.hmcts.reform.pt.dto.HearingInspectionDetailsDto;
import uk.gov.hmcts.reform.pt.dto.NoticeOfRentIncreaseDto;
import uk.gov.hmcts.reform.pt.dto.TenantDetailsDto;
import uk.gov.hmcts.reform.pt.entity.CaseApplicationEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyContactPreferenceEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.DocumentEntity;
import uk.gov.hmcts.reform.pt.entity.NoticeOfRentChangeEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.PropertyInspectionEntity;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.exception.CasePartyNotFoundException;

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
                    : null
            )
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
                    .orElse(null)
            )
            .noticeLegallyValid(entity.getNoticeLegallyValid())
            .noticeNotLegallyValidDetails(entity.getNoticeNotLegallyValidDetails())
            .noticeNotLegallyValidDocument(
                findDocumentOfType(DocumentType.NOTICE_NOT_LEGALLY_VALID_EVIDENCE, ptCaseEntity)
                    .map(ApplicationMapper::mapDocument)
                    .orElse(null)
            )
            .rentIncreaseToCauseHardship(entity.getRentIncreaseToCauseHardship())
            .rentIncreaseToCauseHardshipDocument(
                findDocumentOfType(DocumentType.HARDSHIP_EVIDENCE, ptCaseEntity)
                    .map(ApplicationMapper::mapDocument)
                    .orElse(null)
            )
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

    private static Optional<DocumentEntity> findDocumentOfType(DocumentType type, PTCaseEntity ptCaseEntity) {
        return ptCaseEntity.getDocuments().stream()
            .filter(document -> document.getDocumentType() == type)
            .findFirst();
    }
}
