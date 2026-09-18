package uk.gov.hmcts.reform.pt.entity.reference;

import jakarta.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
public abstract class ReferenceDataEnCyEntity extends ReferenceDataEnOnlyEntity {
    private String valueCy;
}

