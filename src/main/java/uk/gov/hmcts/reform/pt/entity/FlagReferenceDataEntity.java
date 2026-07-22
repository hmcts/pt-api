package uk.gov.hmcts.reform.pt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "flag_ref_data")
public class FlagReferenceDataEntity extends AuditableEntity {
    private Integer flagCode;

    @Column(length = 100)
    private String name;

    @Column(length = 100)
    private String nameCy;

    @Column(length = 10)
    private String availableExternally;

    @Column(length = 10)
    private String visibility;

    @Column(length = 100)
    private String hearingRelevant;
}
