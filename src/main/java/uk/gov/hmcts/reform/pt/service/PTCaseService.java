package uk.gov.hmcts.reform.pt.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.ccd.domain.ApplicantContactPreferences;
import uk.gov.hmcts.reform.pt.ccd.domain.CurrentRentDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.HearingPropertyInspectionDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.MarketRentDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.NoticeOfRentIncreaseDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyDetails;
import uk.gov.hmcts.reform.pt.ccd.domain.TenantDetails;
import uk.gov.hmcts.reform.pt.entity.AddressEntity;
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
    private final MarketRentCaseService marketRentCaseService;

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

        updateContactPreferences(ptCase, caseParty);
        updateTenantDetails(ptCase, caseParty);

        PTCaseEntity ptCaseEntity = caseParty.getPtCase();
        updateHearingOrPropertyInspectionDetails(ptCase, ptCaseEntity);
        updateNoticeOfRentChangeDetails(ptCase, ptCaseEntity);
        updatePropertyDetails(ptCase, ptCaseEntity, caseParty);
        updateCurrentRentDetails(ptCase, ptCaseEntity);
        updateMarketRentDetails(ptCase, ptCaseEntity);
    }

    @Transactional
    public void updateContactPreferences(PTCase ptCase, CasePartyEntity caseParty) {
        ApplicantContactPreferences contactPreferenceData = ptCase.getApplicantContactPreferences();
        if (contactPreferenceData == null) {
            return;
        }

        contactPreferencesService.updateContactPreferences(caseParty, contactPreferenceData);

        caseParty.setPhoneNumber(contactPreferenceData.getPhoneNumberForCalls());
        caseParty.setMobilePhoneNumber(contactPreferenceData.getTextUpdatesPhoneNumber());
        casePartyRepository.save(caseParty);
    }

    @Transactional
    public void updateTenantDetails(PTCase ptCase, CasePartyEntity caseParty) {
        TenantDetails tenantDetails = ptCase.getTenantDetails();
        if (tenantDetails == null) {
            return;
        }

        caseParty.setOrganisationName(tenantDetails.getCompanyName());
        caseParty.setReferenceNumber(tenantDetails.getReferenceNumberForCommunications());
        casePartyRepository.save(caseParty);
    }

    @Transactional
    public void updateHearingOrPropertyInspectionDetails(PTCase ptCase, PTCaseEntity ptCaseEntity) {
        HearingPropertyInspectionDetails hearingOrPropertyInspectionDetails = ptCase.getHearingInspectionDetails();
        if (hearingOrPropertyInspectionDetails == null) {
            return;
        }

        ptCaseEntity.setHearingRequested(hearingOrPropertyInspectionDetails.getHearingRequested());
        ptCaseRepository.save(ptCaseEntity);

        propertyInspectionService.updatePropertyInspection(ptCaseEntity, hearingOrPropertyInspectionDetails);
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

        AddressEntity address = caseParty.getAddresses().stream()
            .findFirst()
            .orElse(new AddressEntity());
        address.setAddressLine1(propertyDetails.getAddressLine1());
        address.setAddressLine2(propertyDetails.getAddressLine2());
        address.setPostTown(propertyDetails.getPostTown());
        address.setCounty(propertyDetails.getCounty());
        address.setPostcode(propertyDetails.getPostcode());
        address.setParty(caseParty);
        address.setPtCase(ptCaseEntity);
        addressRepository.save(address);

        tenancyDetailsService.updateWithPropertyDetails(ptCaseEntity, propertyDetails);
        marketRentCaseService.updateWithPropertyDetails(ptCaseEntity, propertyDetails);
        documentService.updateDocumentsForPropertyDetails(propertyDetails, ptCaseEntity);
    }

    @Transactional
    public void updateCurrentRentDetails(PTCase ptCase, PTCaseEntity ptCaseEntity) {
        CurrentRentDetails currentRentDetails = ptCase.getCurrentRentDetails();
        if (currentRentDetails == null) {
            return;
        }

        tenancyDetailsService.updateWithCurrentRentDetails(ptCaseEntity, currentRentDetails);
        marketRentCaseService.updateWithCurrentRentDetails(ptCaseEntity, currentRentDetails);
    }

    @Transactional
    public void updateMarketRentDetails(PTCase ptCase, PTCaseEntity ptCaseEntity) {
        MarketRentDetails marketRentDetails = ptCase.getMarketRentDetails();
        if (marketRentDetails == null) {
            return;
        }

        marketRentCaseService.updateWithMarketRentDetails(ptCaseEntity, marketRentDetails);
        documentService.updateDocumentsForMarketRentDetails(marketRentDetails, ptCaseEntity);
    }
}
