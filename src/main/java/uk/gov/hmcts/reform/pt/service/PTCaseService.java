package uk.gov.hmcts.reform.pt.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicantContactPreferences;
import uk.gov.hmcts.reform.pt.ccd.domain.HearingPropertyInspectionDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.NoticeOfRentIncreaseDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.TenantDetails;
import uk.gov.hmcts.reform.pt.entity.CaseApplicationEntity;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.CaseTypeEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.repository.CaseApplicationRepository;
import uk.gov.hmcts.reform.pt.repository.AddressRepository;
import uk.gov.hmcts.reform.pt.repository.CasePartyRepository;
import uk.gov.hmcts.reform.pt.repository.PTCaseRepository;

import java.util.UUID;

@Service
@AllArgsConstructor
public class PTCaseService {

    private final CasePartyService casePartyService;
    private final CaseTypeService caseTypeService;
    private final TenancyDetailsService tenancyDetailsService;
    private final PTCaseRepository ptCaseRepository;
    private final CaseApplicationRepository caseApplicationRepository;
    private final CasePartyRepository casePartyRepository;
    private final AddressRepository addressRepository;
    private final ContactPreferencesService contactPreferencesService;
    private final PropertyInspectionService propertyInspectionService;
    private final NoticeOfRentChangeService noticeOfRentChangeService;
    private final DocumentService documentService;

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

        CasePartyEntity caseParty = casePartyService.createCaseParty(ptCaseEntity, ptCase, userId);

        CaseTypeEntity caseType = caseTypeService.getCaseTypeOrCreateIfNotExists(ptCase.getApplicationType());
        CaseApplicationEntity application = CaseApplicationEntity.builder()
            .caseParty(caseParty)
            .caseType(caseType)
            .build();
        caseApplicationRepository.save(application);
    }

    @Transactional
    public void updateCase(long caseReference, PTCase ptCase) {
        CasePartyEntity caseParty = casePartyRepository.findFirstByPtCaseCaseReference(caseReference)
            .orElseThrow(() -> new CaseNotFoundException(caseReference));

        caseParty.setFirstName(ptCase.getApplicantFirstName());
        caseParty.setLastName(ptCase.getApplicantLastName());
        caseParty.setEmailAddress(ptCase.getEmail());
        casePartyRepository.save(caseParty);

        caseParty.getAddresses().stream().findFirst().ifPresent(address -> {
            address.setPostcode(ptCase.getPostcode());
            addressRepository.save(address);
        });

        updateContactPreferences(ptCase, caseParty);
        updateTenantDetails(ptCase, caseParty);

        PTCaseEntity ptCaseEntity = caseParty.getPtCase();
        updateHearingOrPropertyInspectionDetails(ptCase, ptCaseEntity);
        updateNoticeOfRentChangeDetails(ptCase, ptCaseEntity);
    }

    @Transactional
    public void updateContactPreferences(PTCase ptCase, CasePartyEntity caseParty) {
        ApplicantContactPreferences contactPreferenceData = ptCase.getApplicantContactPreferences();

        contactPreferencesService.updateContactPreferences(caseParty, contactPreferenceData);

        caseParty.setPhoneNumber(contactPreferenceData.getPhoneNumberForCalls());
        caseParty.setMobilePhoneNumber(contactPreferenceData.getTextUpdatesPhoneNumber());
        casePartyRepository.save(caseParty);
    }

    @Transactional
    public void updateTenantDetails(PTCase ptCase, CasePartyEntity caseParty) {
        TenantDetails tenantDetails = ptCase.getTenantDetails();
        caseParty.setOrganisationName(tenantDetails.getCompanyName());
        caseParty.setReferenceNumber(tenantDetails.getReferenceNumberForCommunications());
        casePartyRepository.save(caseParty);
    }

    @Transactional
    public void updateHearingOrPropertyInspectionDetails(PTCase ptCase, PTCaseEntity ptCaseEntity) {
        HearingPropertyInspectionDetails hearingOrPropertyInspectionDetails = ptCase.getHearingInspectionDetails();

        ptCaseEntity.setHearingRequested(hearingOrPropertyInspectionDetails.getHearingRequested());
        ptCaseRepository.save(ptCaseEntity);

        propertyInspectionService.updatePropertyInspection(ptCaseEntity, hearingOrPropertyInspectionDetails);
    }

    @Transactional
    public void updateNoticeOfRentChangeDetails(PTCase ptCase, PTCaseEntity ptCaseEntity) {
        NoticeOfRentIncreaseDetails noticeOfRentIncreaseDetails = ptCase.getNoticeOfRentIncreaseDetails();
        if (noticeOfRentIncreaseDetails.getReceivedLandlordNoticeProposingNewRent() == null
            || noticeOfRentIncreaseDetails.getNoticeLegallyValid() == null
            || noticeOfRentIncreaseDetails.getRentIncreaseToCauseHardship() == null) {
            // cannot create notice of rent change details without all the required fields
            return;
        }

        noticeOfRentChangeService.updateNoticeOfRentChangeDetails(noticeOfRentIncreaseDetails, ptCaseEntity);
        documentService.updateDocumentsForNoticeOfRentChange(noticeOfRentIncreaseDetails, ptCaseEntity);
    }
}
