package uk.gov.hmcts.reform.pt.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;
import uk.gov.hmcts.reform.pt.entity.PTCaseEntity;
import uk.gov.hmcts.reform.pt.entity.PartyAccessCodeEntity;
import uk.gov.hmcts.reform.pt.exception.AccessCodeAlreadyUsedException;
import uk.gov.hmcts.reform.pt.repository.PartyAccessCodeRepository;
import uk.gov.hmcts.reform.pt.util.AccessCodeGenerator;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RespondentAccessCodeServiceTest {

    private static final String PLAIN_CODE = "ABCDEFGHJKLM";
    private static final Instant NOW = Instant.parse("2026-10-14T10:00:00Z");

    @Mock
    private AccessCodeGenerator generator;

    @Mock
    private PartyAccessCodeRepository repository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(4);

    private RespondentAccessCodeService service;
    private PTCaseEntity ptCase;
    private CasePartyEntity landlord;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
        service = new RespondentAccessCodeService(generator, encoder, repository, clock, Duration.ofDays(180));

        ptCase = PTCaseEntity.builder().id(10L).build();
        landlord = CasePartyEntity.builder().id(1L).ptCase(ptCase).build();
    }

    @Test
    void shouldCreateNewHashedCodeExpiringIn28Days() {
        when(generator.generate()).thenReturn(PLAIN_CODE);
        when(repository.findByPtCaseIdAndPartyId(10L, 1L)).thenReturn(Optional.empty());

        String returned = service.createFor(ptCase, landlord);

        ArgumentCaptor<PartyAccessCodeEntity> captor = ArgumentCaptor.forClass(PartyAccessCodeEntity.class);
        verify(repository).save(captor.capture());
        PartyAccessCodeEntity saved = captor.getValue();

        assertThat(returned).isEqualTo(PLAIN_CODE);
        assertThat(saved.getPtCase()).isSameAs(ptCase);
        assertThat(saved.getParty()).isSameAs(landlord);
        assertThat(encoder.matches(PLAIN_CODE, saved.getCode())).isTrue();
        assertThat(saved.getExpiresAt()).isEqualTo(LocalDateTime.of(2027, 4, 12, 10, 0));
        assertThat(saved.getUsedAt()).isNull();
    }

    @Test
    void shouldRejectPartyFromAnotherCase() {
        PTCaseEntity otherCase = PTCaseEntity.builder().id(20L).build();
        CasePartyEntity partyOnOtherCase = CasePartyEntity.builder().id(2L).ptCase(otherCase).build();

        assertThatThrownBy(() -> service.createFor(ptCase, partyOnOtherCase))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("10");

        verify(repository, never()).save(any());
    }

    @Test
    void shouldRefuseReissueWhenPartyAlreadyLinked() {
        landlord.setIdamId(UUID.randomUUID());

        assertThatThrownBy(() -> service.createFor(ptCase, landlord))
            .isInstanceOf(AccessCodeAlreadyUsedException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void shouldReplaceCodeAndRestartExpiryWhenReissuing() {
        String oldCode = "OLDCODE23456";
        PartyAccessCodeEntity existing = PartyAccessCodeEntity.builder()
            .id(99L).ptCase(ptCase).party(landlord)
            .code(encoder.encode(oldCode)).expiresAt(LocalDateTime.of(2026, 10, 24, 10, 0))
            .build();
        when(generator.generate()).thenReturn(PLAIN_CODE);
        when(repository.findByPtCaseIdAndPartyId(10L, 1L)).thenReturn(Optional.of(existing));

        String returned = service.createFor(ptCase, landlord);

        verify(repository).save(existing);
        assertThat(returned).isEqualTo(PLAIN_CODE);
        assertThat(existing.getExpiresAt()).isEqualTo(LocalDateTime.of(2027, 4, 12, 10, 0));
        assertThat(encoder.matches(PLAIN_CODE, existing.getCode())).isTrue();
        assertThat(encoder.matches(oldCode, existing.getCode())).isFalse();
    }

    @Test
    void shouldIssueNewCodeWhenPreviousCodeExpired() {
        PartyAccessCodeEntity expired = PartyAccessCodeEntity.builder()
            .id(99L).ptCase(ptCase).party(landlord)
            .code("old-hash").expiresAt(LocalDateTime.of(2026, 10, 1, 10, 0))
            .build();
        when(generator.generate()).thenReturn(PLAIN_CODE);
        when(repository.findByPtCaseIdAndPartyId(10L, 1L)).thenReturn(Optional.of(expired));

        String returned = service.createFor(ptCase, landlord);

        assertThat(returned).isEqualTo(PLAIN_CODE);
        assertThat(expired.getExpiresAt()).isEqualTo(LocalDateTime.of(2027, 4, 12, 10, 0));
        assertThat(encoder.matches(PLAIN_CODE, expired.getCode())).isTrue();
    }

    @Test
    void shouldRefuseReissueWhenCodeAlreadyUsed() {
        PartyAccessCodeEntity used = PartyAccessCodeEntity.builder()
            .id(99L).ptCase(ptCase).party(landlord)
            .code("old-hash").expiresAt(LocalDateTime.of(2027, 1, 1, 10, 0))
            .usedAt(LocalDateTime.of(2026, 10, 10, 10, 0))
            .build();
        when(repository.findByPtCaseIdAndPartyId(10L, 1L)).thenReturn(Optional.of(used));

        assertThatThrownBy(() -> service.createFor(ptCase, landlord))
            .isInstanceOf(AccessCodeAlreadyUsedException.class);

        assertThat(used.getCode()).isEqualTo("old-hash");
        verify(repository, never()).save(any());
    }
}
