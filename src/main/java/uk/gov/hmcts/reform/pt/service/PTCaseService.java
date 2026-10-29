package uk.gov.hmcts.reform.pt.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicantContactPreferences;
import uk.gov.hmcts.reform.pt.ccd.domain.RentDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.HearingPropertyInspectionDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.LandlordDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.NoticeOfRentIncreaseDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyRole;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.TenancyAgreementDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.TenantDetails;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.repository.CasePartyRepository;
import uk.gov.hmcts.reform.pt.repository.PTCaseRepository;
import uk.gov.hmcts.reform.pt.repository.PartyAccessCodeRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static uk.gov.hmcts.reform.pt.util.NullSafeSetter.setIfNotNull;

@Service
@AllArgsConstructor
public class PTCaseService {

    private final CasePartyService casePartyService;
    private final TenancyDetailsService tenancyDetailsService;
    private final PTCaseRepository ptCaseRepository;
    private final CasePartyRepository casePartyRepository;
    private final PartyAccessCodeRepository partyAccessCodeRepository;
    private final NoticeOfRentChangeService noticeOfRentChangeService;
    private final DocumentService documentService;
    private final MarketRentCaseService marketRentCaseService;
    private final AddressService addressService;
    private final DocumentUploadValidator documentUploadValidator;

    @Transactional
    public void createCase(
        long caseReference,
        UUID userId,
        PTCase ptCase
    ) {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .caseReference(caseReference)
            .build();
        ptCaseRepository.save(ptCaseEntity);

        tenancyDetailsService.getTenancyDetailsOrCreateIfNotExists(ptCase.getTenancyType(), ptCaseEntity);

        casePartyService.createApplicantCaseParty(ptCaseEntity, ptCase, userId);

        marketRentCaseService.updateWithApplicationType(ptCaseEntity, ptCase.getApplicationType());
    }

    @Transactional(readOnly = true)
    public Optional<PTCaseEntity> fetchCaseByCaseReference(long caseReference) {
        return ptCaseRepository.findByCaseReference(caseReference);
    }

    @Transactional
    public void deleteDocumentsForCase(long caseReference) {
        documentService.findAllForCase(caseReference)
            .forEach(document -> documentService.deleteDocument(document.getId(), caseReference));
    }

    @Transactional
    public void deleteCase(long caseReference) {
        PTCaseEntity ptCaseEntity = ptCaseRepository.findByCaseReference(caseReference)
            .orElseThrow(() -> new CaseNotFoundException(caseReference));

        partyAccessCodeRepository.deleteAll(partyAccessCodeRepository.findAllByPtCaseCaseReference(caseReference));

        ptCaseRepository.delete(ptCaseEntity);
    }

    @Transactional
    public void updateCase(long caseReference, PTCase ptCase) {
        PTCaseEntity ptCaseEntity = ptCaseRepository.findByCaseReference(caseReference)
            .orElseThrow(() -> new CaseNotFoundException(caseReference));

        CasePartyEntity applicantCaseParty = casePartyService.getPartyForCaseByRole(ptCaseEntity, PartyRole.APPLICANT)
            .orElseThrow(() -> new CaseNotFoundException(caseReference));

        setIfNotNull(ptCase.getApplicantFirstName(), applicantCaseParty::setFirstName);
        setIfNotNull(ptCase.getApplicantLastName(), applicantCaseParty::setLastName);
        setIfNotNull(ptCase.getEmail(), applicantCaseParty::setEmailAddress);
        casePartyRepository.save(applicantCaseParty);

        this.updateContactPreferences(ptCase, applicantCaseParty);
        this.updateTenantDetails(ptCase, applicantCaseParty);
        this.updateHearingOrPropertyInspectionDetails(ptCase, ptCaseEntity);
        this.updateNoticeOfRentChangeDetails(ptCase, ptCaseEntity);
        this.updatePropertyDetails(ptCase, ptCaseEntity, applicantCaseParty);
        this.updateRentDetails(ptCase, ptCaseEntity);
        this.updateTenancyAgreementDetails(ptCase, ptCaseEntity);
        this.updateLandlordDetails(ptCase, ptCaseEntity);
    }

    @Transactional
    public List<String> updateDocuments(long caseReference, PTCase ptCase) {
        PTCaseEntity ptCaseEntity = ptCaseRepository.findByCaseReference(caseReference)
            .orElseThrow(() -> new CaseNotFoundException(caseReference));

        final Map<Long, String> urlsBefore = documentUploadValidator.snapshot(caseReference);

        PropertyDetails propertyDetails = ptCase.getPropertyDetails();
        if (propertyDetails != null) {
            documentService.updateDocumentsForPropertyDetails(propertyDetails, ptCaseEntity);
        }

        NoticeOfRentIncreaseDetails noticeOfRentIncreaseDetails = ptCase.getNoticeOfRentIncreaseDetails();
        if (noticeOfRentIncreaseDetails != null) {
            documentService.updateDocumentsForNoticeOfRentChange(noticeOfRentIncreaseDetails, ptCaseEntity);
        }

        TenancyAgreementDetails tenancyAgreementDetails = ptCase.getTenancyAgreementDetails();
        if (tenancyAgreementDetails != null) {
            documentService.updateDocumentsForTenancyAgreementDetails(tenancyAgreementDetails, ptCaseEntity);
        }

        RentDetails rentDetails = ptCase.getRentDetails();
        if (rentDetails != null) {
            documentService.updateDocumentsForRentDetails(rentDetails, ptCaseEntity);
        }

        return documentUploadValidator.errors(caseReference, urlsBefore);
    }

    @Transactional
    public void deleteDocument(long caseReference, PTCase ptCase) {
        String documentId = ptCase.getDocumentIdToDelete();
        if (documentId == null || documentId.isBlank()) {
            throw new IllegalArgumentException("No document id supplied for deletion");
        }

        documentService.deleteDocument(Long.parseLong(documentId), caseReference);
    }

    @Transactional
    public void updateContactPreferences(PTCase ptCase, CasePartyEntity caseParty) {
        ApplicantContactPreferences contactPreferenceData = ptCase.getApplicantContactPreferences();
        if (contactPreferenceData == null) {
            return;
        }

        setIfNotNull(contactPreferenceData.getTextUpdates(), caseParty::setContactByText);
        caseParty.setPhoneNumber(contactPreferenceData.getPhoneNumberForCalls());
        caseParty.setMobilePhoneNumber(contactPreferenceData.getTextUpdates().toBoolean()
                                           ? contactPreferenceData.getTextUpdatesPhoneNumber()
                                           : null);

        casePartyRepository.save(caseParty);
    }

    @Transactional
    public void updateTenantDetails(PTCase ptCase, CasePartyEntity caseParty) {
        TenantDetails tenantDetails = ptCase.getTenantDetails();
        if (tenantDetails == null) {
            return;
        }

        setIfNotNull(tenantDetails.getCompanyName(), caseParty::setOrganisationName);
        setIfNotNull(
            tenantDetails.getReferenceNumberForCommunications(),
            caseParty::setReferenceNumberForCommunications
        );
        casePartyRepository.save(caseParty);
    }

    @Transactional
    public void updateHearingOrPropertyInspectionDetails(PTCase ptCase, PTCaseEntity ptCaseEntity) {
        HearingPropertyInspectionDetails hearingOrPropertyInspectionDetails = ptCase.getHearingInspectionDetails();
        if (hearingOrPropertyInspectionDetails == null) {
            return;
        }

        ptCaseRepository.save(ptCaseEntity);

        marketRentCaseService.updateWithPropertyInspectionDetails(ptCaseEntity, hearingOrPropertyInspectionDetails);
    }

    @Transactional
    public void updateNoticeOfRentChangeDetails(PTCase ptCase, PTCaseEntity ptCaseEntity) {
        NoticeOfRentIncreaseDetails noticeOfRentIncreaseDetails = ptCase.getNoticeOfRentIncreaseDetails();
        if (noticeOfRentIncreaseDetails == null) {
            return;
        }

        noticeOfRentChangeService.updateNoticeOfRentChangeDetails(noticeOfRentIncreaseDetails, ptCaseEntity);
        documentService.updateDocumentsForNoticeOfRentChange(noticeOfRentIncreaseDetails, ptCaseEntity);
    }

    @Transactional
    public void updatePropertyDetails(PTCase ptCase, PTCaseEntity ptCaseEntity, CasePartyEntity caseParty) {
        PropertyDetails propertyDetails = ptCase.getPropertyDetails();
        if (propertyDetails == null) {
            return;
        }

        PartyDetails partyDetails = PartyDetails.builder()
            .addressLine1(propertyDetails.getAddressLine1())
            .addressLine2(propertyDetails.getAddressLine2())
            .postTown(propertyDetails.getPostTown())
            .county(propertyDetails.getCounty())
            .postcode(propertyDetails.getPostcode())
            .build();
        addressService.updatePropertyAddress(partyDetails, caseParty, ptCaseEntity);

        tenancyDetailsService.updateWithPropertyDetails(ptCaseEntity, propertyDetails);
        marketRentCaseService.updateWithPropertyDetails(ptCaseEntity, propertyDetails);
        documentService.updateDocumentsForPropertyDetails(propertyDetails, ptCaseEntity);
    }

    @Transactional
    public void updateRentDetails(PTCase ptCase, PTCaseEntity ptCaseEntity) {
        RentDetails rentDetails = ptCase.getRentDetails();
        if (rentDetails == null) {
            return;
        }

        tenancyDetailsService.updateWithRentDetails(ptCaseEntity, rentDetails);
        marketRentCaseService.updateWithRentDetails(ptCaseEntity, rentDetails);
        documentService.updateDocumentsForRentDetails(rentDetails, ptCaseEntity);
    }

    @Transactional
    public void updateTenancyAgreementDetails(PTCase ptCase, PTCaseEntity ptCaseEntity) {
        TenancyAgreementDetails tenancyAgreementDetails = ptCase.getTenancyAgreementDetails();
        if (tenancyAgreementDetails == null) {
            return;
        }

        tenancyDetailsService.updateWithTenancyAgreementDetails(ptCaseEntity, tenancyAgreementDetails);
        documentService.updateDocumentsForTenancyAgreementDetails(tenancyAgreementDetails, ptCaseEntity);
    }

    @Transactional
    public void updateLandlordDetails(PTCase ptCase, PTCaseEntity ptCaseEntity) {
        LandlordDetails landlordDetails = ptCase.getLandlordDetails();
        if (landlordDetails == null) {
            return;
        }

        casePartyService.updateWithLandlordDetails(ptCaseEntity, landlordDetails);

        setIfNotNull(landlordDetails.getRepresentativeType(), ptCaseEntity::setLandlordType);
        ptCaseRepository.save(ptCaseEntity);
    }
}
