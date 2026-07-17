package uk.gov.hmcts.reform.pt.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

import java.util.UUID;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "case_party_access")
public class CasePartyAccessEntity extends AuditableEntity {
    private UUID idamId;

    @Column(length = 100)
    private String accessRole;

    private Integer accessCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_party_id")
    @JsonBackReference
    private CasePartyEntity party;
}
