package uk.gov.hmcts.reform.pt.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.ccd.client.model.StartEventResponse;
import uk.gov.hmcts.reform.pt.ccd.api.CcdApiClient;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.pt.dto.CaseDto;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.exception.InvalidCaseReferenceException;
import uk.gov.hmcts.reform.pt.mapper.PTCaseMapper;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.event.EventId;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.model.CreateApplicationRequest;
import uk.gov.hmcts.reform.pt.model.CreateApplicationResponse;
import uk.gov.hmcts.reform.pt.repository.PTCaseRepository;
import uk.gov.hmcts.reform.pt.util.CaseReferenceUtils;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PTCaseService {

    private final PTCaseRepository ptCaseRepository;
    private final CcdApiClient ccdApi;

    public void createCase(
        long caseReference,
        UUID userId,
        PTCase ptCase
    ) {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .caseReference(caseReference)
            .applicantIdamUserId(userId)
            .applicantFirstName(ptCase.getFirstName())
            .applicantLastName(ptCase.getLastName())
            .email(ptCase.getEmail())
            .postcode(ptCase.getPostcode())
            .applicationType(ptCase.getApplicationType())
            .build();
        ptCaseRepository.save(ptCaseEntity);
    }

    public CreateApplicationResponse createCase(CreateApplicationRequest request, UUID userId) {
        PTCase ptCase = PTCase.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .postcode(request.getPostcode())
            .applicationType(request.getApplicationType())
            .build();
        StartEventResponse startEventResponse = ccdApi.startEvent(EventId.createNewApplication);
        CaseDetails caseDetails = ccdApi.submitCaseCreation(
            ptCase,
            EventId.createNewApplication,
            startEventResponse.getToken()
        );

        Long caseReference = caseDetails.getId();
        createCase(caseReference, userId, ptCase);

        return CreateApplicationResponse.builder()
            .caseReference(caseReference)
            .build();
    }

    @Transactional
    public List<CaseDto> getCasesForUser(UUID userId) {
        return ptCaseRepository.findAllByApplicantIdamUserId(userId).stream()
            .map(PTCaseMapper::toDto)
            .toList();
    }

    @Transactional
    public CaseDto getCaseByCaseReference(long caseReference, UUID userId) {
        if (!CaseReferenceUtils.isValidCaseReference(caseReference)) {
            throw new InvalidCaseReferenceException(caseReference);
        }

        return ptCaseRepository.findByCaseReferenceAndApplicantIdamUserId(caseReference, userId)
            .map(PTCaseMapper::toDto)
            .orElseThrow(() -> new CaseNotFoundException(caseReference));
    }
}
