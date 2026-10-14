package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.ccd.client.CaseAssignmentApi;
import uk.gov.hmcts.reform.ccd.client.model.CaseAssignmentUserRolesRequest;
import uk.gov.hmcts.reform.pt.ccd.domain.UserRole;
import uk.gov.hmcts.reform.pt.security.IdamTokenProvider;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaseRoleAssignmentServiceTest {

    @Mock
    private CaseAssignmentApi caseAssignmentApi;

    @Mock
    private AuthTokenGenerator authTokenGenerator;

    @Mock
    private IdamTokenProvider idamTokenProvider;

    @InjectMocks
    private CaseRoleAssignmentService service;

    @Test
    void shouldAssignCaseRoleAsSystemUser() {
        UUID userId = UUID.randomUUID();
        when(idamTokenProvider.getAuthToken()).thenReturn("Bearer system-token");
        when(authTokenGenerator.generate()).thenReturn("s2s-token");

        service.assignCaseRole(1234567890123456L, userId, UserRole.DEFENDANT);

        ArgumentCaptor<CaseAssignmentUserRolesRequest> captor =
            ArgumentCaptor.forClass(CaseAssignmentUserRolesRequest.class);
        verify(caseAssignmentApi).addCaseUserRoles(eq("Bearer system-token"), eq("s2s-token"), captor.capture());

        assertThat(captor.getValue().getCaseAssignmentUserRolesWithOrganisation())
            .singleElement()
            .satisfies(role -> {
                assertThat(role.getCaseDataId()).isEqualTo("1234567890123456");
                assertThat(role.getUserId()).isEqualTo(userId.toString());
                assertThat(role.getCaseRole()).isEqualTo("[DEFENDANT]");
            });
    }
}
