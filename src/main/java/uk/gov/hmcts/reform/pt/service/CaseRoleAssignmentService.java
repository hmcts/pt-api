package uk.gov.hmcts.reform.pt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.CaseAssignmentApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseAssignmentUserRoleWithOrganisation;
import uk.gov.hmcts.reform.ccd.client.model.CaseAssignmentUserRolesRequest;
import uk.gov.hmcts.reform.pt.ccd.domain.UserRole;
import uk.gov.hmcts.reform.pt.security.IdamTokenProvider;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaseRoleAssignmentService {

    private final CaseAssignmentApi caseAssignmentApi;
    private final AuthTokenGenerator authTokenGenerator;
    private final IdamTokenProvider idamTokenProvider;

    public void assignCaseRole(long caseReference, UUID userId, UserRole role) {
        CaseAssignmentUserRolesRequest request = CaseAssignmentUserRolesRequest.builder()
            .caseAssignmentUserRolesWithOrganisation(List.of(
                CaseAssignmentUserRoleWithOrganisation.builder()
                    .caseDataId(String.valueOf(caseReference))
                    .userId(userId.toString())
                    .caseRole(role.getRole())
                    .build()))
            .build();

        caseAssignmentApi.addCaseUserRoles(
            idamTokenProvider.getAuthToken(),
            authTokenGenerator.generate(),
            request);
    }
}
