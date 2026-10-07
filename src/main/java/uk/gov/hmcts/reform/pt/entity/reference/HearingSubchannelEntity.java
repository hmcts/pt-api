package uk.gov.hmcts.reform.pt.entity.reference;

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
@Table(name = "hearing_subchannel")
public class HearingSubchannelEntity extends ReferenceDataEnCyEntity {
    @Column(name = "parentcategory", length = 64)
    private String parentCategory;

    @Column(name = "parentkey", length = 64)
    private String parentKey;
}
