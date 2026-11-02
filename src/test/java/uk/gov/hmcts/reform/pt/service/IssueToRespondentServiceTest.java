package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyRole;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.exception.AccessCodeAlreadyUsedException;
import uk.gov.hmcts.reform.pt.exception.CaseNotFoundException;
import uk.gov.hmcts.reform.pt.notify.notification.LandlordAccessCodeNotification;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.pt.ccd.domain.PartyRole.APPLICANT;
import static uk.gov.hmcts.reform.pt.ccd.domain.PartyRole.LANDLORD;

@ExtendWith(MockitoExtension.class)
class IssueToRespondentServiceTest {

    private static final long CASE_REFERENCE = 1234567890123456L;
    private static final String ACCESS_CODE = "ABCDEFGHJKLM";

    @Mock
    private PTCaseService ptCaseService;

    @Mock
    private RespondentAccessCodeService respondentAccessCodeService;

    @Mock
    private LandlordAccessCodeNotification landlordAccessCodeNotification;

    @InjectMocks
    private IssueToRespondentService service;

    private PTCaseEntity ptCase;

    @BeforeEach
    void setUp() {
        ptCase = PTCaseEntity.builder().id(10L).caseReference(CASE_REFERENCE).build();
    }

    @Test
    void shouldCreateAccessCodeThenNotifyLandlord() {
        CasePartyEntity landlord = party(2L, LANDLORD);
        when(ptCaseService.fetchCaseByCaseReference(CASE_REFERENCE)).thenReturn(Optional.of(ptCase));
        when(respondentAccessCodeService.createFor(ptCase, landlord)).thenReturn(ACCESS_CODE);

        CasePartyEntity applicant = party(1L, APPLICANT);

        service.issueToRespondent(CASE_REFERENCE);

        InOrder inOrder = inOrder(respondentAccessCodeService, landlordAccessCodeNotification);
        inOrder.verify(respondentAccessCodeService).createFor(ptCase, landlord);
        inOrder.verify(landlordAccessCodeNotification).send(ptCase, applicant, landlord, ACCESS_CODE);
    }

    @Test
    void shouldThrowWhenCaseNotFound() {
        when(ptCaseService.fetchCaseByCaseReference(CASE_REFERENCE)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.issueToRespondent(CASE_REFERENCE))
            .isInstanceOf(CaseNotFoundException.class)
            .hasMessageContaining(String.valueOf(CASE_REFERENCE));

        verifyNoInteractions(respondentAccessCodeService, landlordAccessCodeNotification);
    }

    @Test
    void shouldThrowWhenCaseHasNoLandlord() {
        party(1L, APPLICANT);
        when(ptCaseService.fetchCaseByCaseReference(CASE_REFERENCE)).thenReturn(Optional.of(ptCase));

        assertThatThrownBy(() -> service.issueToRespondent(CASE_REFERENCE))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("LANDLORD");

        verifyNoInteractions(respondentAccessCodeService, landlordAccessCodeNotification);
    }

    @Test
    void shouldThrowWhenCaseHasNoApplicant() {
        party(2L, LANDLORD);
        when(ptCaseService.fetchCaseByCaseReference(CASE_REFERENCE)).thenReturn(Optional.of(ptCase));

        assertThatThrownBy(() -> service.issueToRespondent(CASE_REFERENCE))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("APPLICANT");

        verifyNoInteractions(respondentAccessCodeService, landlordAccessCodeNotification);
    }

    @Test
    void shouldNotNotifyWhenAccessCodeCannotBeCreated() {
        party(1L, APPLICANT);
        CasePartyEntity landlord = party(2L, LANDLORD);
        when(ptCaseService.fetchCaseByCaseReference(CASE_REFERENCE)).thenReturn(Optional.of(ptCase));
        when(respondentAccessCodeService.createFor(ptCase, landlord))
            .thenThrow(new AccessCodeAlreadyUsedException("This access code has already been used"));

        assertThatThrownBy(() -> service.issueToRespondent(CASE_REFERENCE))
            .isInstanceOf(AccessCodeAlreadyUsedException.class);

        verify(landlordAccessCodeNotification, never()).send(any(), any(), any(), any());
    }

    private CasePartyEntity party(long id, PartyRole role) {
        CasePartyEntity party = CasePartyEntity.builder().id(id).ptCase(ptCase).casePartyRole(role).build();
        ptCase.getParties().add(party);
        return party;
    }
}
