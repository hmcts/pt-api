package uk.gov.hmcts.reform.pt.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "case_party_address")
public class CasePartyAddressEntity extends AbstractAddressEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_party_id", nullable = false, unique = true)
    @JsonBackReference
    private CasePartyEntity party;
}
