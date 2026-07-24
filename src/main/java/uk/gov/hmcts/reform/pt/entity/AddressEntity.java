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

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "address")
public class AddressEntity extends AuditableEntity {
    @Column(name = "address_line_1", length = 100)
    private String addressLine1;

    @Column(name = "address_line_2", length = 100)
    private String addressLine2;

    @Column(name = "address_line_3", length = 100)
    private String addressLine3;

    @Column(length = 100)
    private String postTown;

    @Column(length = 100)
    private String county;

    @Column(name = "post_code", length = 10)
    private String postcode;

    @Column(length = 100)
    private String country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_party_id", nullable = false)
    @JsonBackReference
    private CasePartyEntity party;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pt_case_id", nullable = false)
    @JsonBackReference
    private PTCaseEntity ptCase;
}
