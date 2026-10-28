package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyRole;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.PartyAccessCodeEntity;
import uk.gov.hmcts.reform.pt.exception.AccessCodeAlreadyUsedException;
import uk.gov.hmcts.reform.pt.exception.AccessCodeExpiredException;
import uk.gov.hmcts.reform.pt.exception.InvalidAccessCodeException;
import uk.gov.hmcts.reform.pt.repository.PartyAccessCodeRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.pt.ccd.accesscontrol.UserRole.RESPONDENT;

@ExtendWith(MockitoExtension.class)
class PartyAccessCodeLinkServiceTest {

    private static final long CASE_REFERENCE = 1234567890123456L;
    private static final String ENTERED_CODE = "ABCDEFGHJKLM";
    private static final String STORED_HASH = "hash";
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 14, 10, 0);
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private PartyAccessCodeRepository repository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private CaseRoleAssignmentService caseRoleAssignmentService;

    private PartyAccessCodeLinkService service;
    private PTCaseEntity ptCase;
    private CasePartyEntity landlord;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(NOW.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        service = new PartyAccessCodeLinkService(repository, encoder, caseRoleAssignmentService, clock);

        ptCase = PTCaseEntity.builder().id(10L).caseReference(CASE_REFERENCE).build();
        landlord = party(1L, PartyRole.LANDLORD, null);
    }

    @Test
    void shouldLinkLandlordAndAssignCaseRole() {
        givenStoredCode(accessCodeFor(landlord, NOW.plusDays(1)));

        service.linkPartyByAccessCode(CASE_REFERENCE, ENTERED_CODE, USER_ID);

        assertThat(landlord.getIdamId()).isEqualTo(USER_ID);
        verify(caseRoleAssignmentService).assignCaseRole(CASE_REFERENCE, USER_ID, RESPONDENT);
    }

    @Test
    void shouldMatchTheCorrectCodeWhenCaseHasSeveral() {
        CasePartyEntity otherParty = party(2L, PartyRole.LANDLORD, null);
        PartyAccessCodeEntity otherCode = accessCodeFor(otherParty, NOW.plusDays(1));
        otherCode.setCode("other-hash");
        PartyAccessCodeEntity landlordCode = accessCodeFor(landlord, NOW.plusDays(1));

        when(repository.findAllByPtCaseCaseReference(CASE_REFERENCE)).thenReturn(List.of(otherCode, landlordCode));
        when(encoder.matches(ENTERED_CODE, "other-hash")).thenReturn(false);
        when(encoder.matches(ENTERED_CODE, STORED_HASH)).thenReturn(true);

        service.linkPartyByAccessCode(CASE_REFERENCE, ENTERED_CODE, USER_ID);

        assertThat(landlord.getIdamId()).isEqualTo(USER_ID);
        assertThat(otherParty.getIdamId()).isNull();
    }

    @Test
    void shouldRejectWhenCaseHasNoCodes() {
        when(repository.findAllByPtCaseCaseReference(CASE_REFERENCE)).thenReturn(List.of());

        assertThatThrownBy(() -> service.linkPartyByAccessCode(CASE_REFERENCE, ENTERED_CODE, USER_ID))
            .isInstanceOf(InvalidAccessCodeException.class);

        verifyNotLinked();
    }

    @Test
    void shouldRejectWhenCodeDoesNotMatch() {
        when(repository.findAllByPtCaseCaseReference(CASE_REFERENCE))
            .thenReturn(List.of(accessCodeFor(landlord, NOW.plusDays(1))));
        when(encoder.matches(ENTERED_CODE, STORED_HASH)).thenReturn(false);

        assertThatThrownBy(() -> service.linkPartyByAccessCode(CASE_REFERENCE, ENTERED_CODE, USER_ID))
            .isInstanceOf(InvalidAccessCodeException.class);

        verifyNotLinked(accessCodeFor(landlord, NOW.plusDays(1)));
    }

    @Test
    void shouldRejectCodeForNonLandlordParty() {
        CasePartyEntity applicant = party(3L, PartyRole.APPLICANT, null);
        givenStoredCode(accessCodeFor(applicant, NOW.plusDays(1)));

        assertThatThrownBy(() -> service.linkPartyByAccessCode(CASE_REFERENCE, ENTERED_CODE, USER_ID))
            .isInstanceOf(InvalidAccessCodeException.class);

        assertThat(applicant.getIdamId()).isNull();
        verify(caseRoleAssignmentService, never()).assignCaseRole(anyLong(), any(), any());
    }

    @Test
    void shouldRejectWhenPartyAlreadyLinked() {
        UUID existingUser = UUID.randomUUID();
        landlord.setIdamId(existingUser);
        givenStoredCode(accessCodeFor(landlord, NOW.plusDays(1)));

        assertThatThrownBy(() -> service.linkPartyByAccessCode(CASE_REFERENCE, ENTERED_CODE, USER_ID))
            .isInstanceOf(AccessCodeAlreadyUsedException.class);

        assertThat(landlord.getIdamId()).isEqualTo(existingUser);
        verify(caseRoleAssignmentService, never()).assignCaseRole(anyLong(), any(), any());
    }

    @Test
    void shouldRejectWhenUserAlreadyLinkedToAnotherPartyOnCase() {
        party(4L, PartyRole.APPLICANT, USER_ID);   // the same user is already the applicant
        givenStoredCode(accessCodeFor(landlord, NOW.plusDays(1)));

        assertThatThrownBy(() -> service.linkPartyByAccessCode(CASE_REFERENCE, ENTERED_CODE, USER_ID))
            .isInstanceOf(AccessCodeAlreadyUsedException.class);

        verifyNotLinked();
    }

    @Test
    void shouldPropagateRoleAssignmentFailure() {
        givenStoredCode(accessCodeFor(landlord, NOW.plusDays(1)));
        doThrow(new RuntimeException("CCD down"))
            .when(caseRoleAssignmentService).assignCaseRole(CASE_REFERENCE, USER_ID, RESPONDENT);

        // In the real app @Transactional rolls back the idamId change when this throws
        assertThatThrownBy(() -> service.linkPartyByAccessCode(CASE_REFERENCE, ENTERED_CODE, USER_ID))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("CCD down");
    }

    @Test
    void shouldLinkLandlordRecordUsageAndAssignCaseRole() {
        PartyAccessCodeEntity accessCode = givenStoredCode(accessCodeFor(landlord, NOW.plusDays(1)));

        service.linkPartyByAccessCode(CASE_REFERENCE, ENTERED_CODE, USER_ID);

        assertThat(landlord.getIdamId()).isEqualTo(USER_ID);
        assertThat(accessCode.getUsedAt()).isEqualTo(NOW);
        verify(caseRoleAssignmentService).assignCaseRole(CASE_REFERENCE, USER_ID, RESPONDENT);
    }

    @Test
    void shouldRejectExpiredCode() {
        PartyAccessCodeEntity accessCode = givenStoredCode(accessCodeFor(landlord, NOW));

        assertThatThrownBy(() -> service.linkPartyByAccessCode(CASE_REFERENCE, ENTERED_CODE, USER_ID))
            .isInstanceOf(AccessCodeExpiredException.class);

        verifyNotLinked(accessCode);
    }

    private CasePartyEntity party(long id, PartyRole role, UUID idamId) {
        CasePartyEntity party = CasePartyEntity.builder()
            .id(id).ptCase(ptCase).casePartyRole(role).idamId(idamId)
            .build();
        ptCase.getParties().add(party);
        return party;
    }

    private PartyAccessCodeEntity accessCodeFor(CasePartyEntity party, LocalDateTime expiresAt) {
        return PartyAccessCodeEntity.builder()
            .ptCase(ptCase).party(party).code(STORED_HASH).expiresAt(expiresAt)
            .build();
    }

    private PartyAccessCodeEntity givenStoredCode(PartyAccessCodeEntity accessCode) {
        when(repository.findAllByPtCaseCaseReference(CASE_REFERENCE)).thenReturn(List.of(accessCode));
        when(encoder.matches(ENTERED_CODE, STORED_HASH)).thenReturn(true);
        return accessCode;
    }

    private void verifyNotLinked() {
        assertThat(landlord.getIdamId()).isNull();
        verify(caseRoleAssignmentService, never()).assignCaseRole(anyLong(), any(), any());
    }

    private void verifyNotLinked(PartyAccessCodeEntity accessCode) {
        assertThat(landlord.getIdamId()).isNull();
        assertThat(accessCode.getUsedAt()).isNull();
        verify(caseRoleAssignmentService, never()).assignCaseRole(anyLong(), any(), any());
    }
}
