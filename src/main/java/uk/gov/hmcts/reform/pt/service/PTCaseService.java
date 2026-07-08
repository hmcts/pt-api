package uk.gov.hmcts.reform.pt.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.CoreCaseDataApi;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.model.CreateApplicationRequest;
import uk.gov.hmcts.reform.pt.repository.PTCaseRepository;

@Service
@AllArgsConstructor
public class PTCaseService {
    private final PTCaseRepository ptCaseRepository;
    private final CoreCaseDataApi coreCaseDataApi;

    public void createCase(
        long caseReference,
        PTCase ptCase
    ) {
        PTCaseEntity ptCaseEntity = PTCaseEntity.builder()
            .caseReference(caseReference)
            .applicantForename(ptCase.getApplicantForename())
            .build();
        ptCaseRepository.save(ptCaseEntity);
    }

//    public void createCase(CreateApplicationRequest request) {
//        coreCaseDataApi.startForCitizen()
//    }
}
