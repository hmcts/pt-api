package uk.gov.hmcts.reform.pt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "party_access_code")
public class PartyAccessCodeEntity extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pt_case_id", nullable = false)
    private PTCaseEntity ptCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_party_id", nullable = false)
    private CasePartyEntity party;

    @Column(nullable = false, length = 100)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime usedAt;

    public boolean isExpired(LocalDateTime now) {
        return !now.isBefore(expiresAt);
    }
}
