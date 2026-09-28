package uk.gov.hmcts.reform.pt.entity.reference;

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
@Table(name = "facilities")
public class FacilitiesEntity extends ReferenceDataEnCyEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hearing_venue_id", nullable = false)
    private HearingVenueEntity hearingVenue;
}
