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

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "case_party_attribute_assertion")
public class CasePartyAttributeEntity extends AuditableEntity {
    @Column(length = 100)
    private String attributeName;

    @Column(length = 100)
    private String originalValue;

    @Column(length = 100)
    private String assertedValue;

    @Column(length = 100)
    private String assertedBy;

    @Column(length = 100)
    private String status;

    private LocalDateTime decidedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_party_id")
    @JsonBackReference
    private CasePartyEntity party;
}
