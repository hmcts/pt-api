package uk.gov.hmcts.reform.pt.entity.reference;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@Table(name = "hearing_priority")
public class HearingPriorityEntity extends ReferenceDataEnCyEntity {
}
