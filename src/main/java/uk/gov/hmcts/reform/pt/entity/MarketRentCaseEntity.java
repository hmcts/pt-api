package uk.gov.hmcts.reform.pt.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uk.gov.hmcts.ccd.sdk.type.YesOrNo;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyType;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "market_rent_case")
public class MarketRentCaseEntity extends AuditableEntity {
    @Version
    private Integer version;

    @Column(length = 100)
    private String method;

    @Column(length = 100)
    private String channel;

    @Column(length = 100)
    private String status;

    private LocalDateTime submittedDate;
    private LocalDateTime issuedDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private YesOrNo propertyFloorPlanAvailable;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private PropertyType typeOfPropertyRenting;

    @Column(length = 500)
    private String rentingFlatDetails;

    @Column(length = 500)
    private String rentingRoomsDetails;

    @Column(length = 500)
    private String otherMethodOfRentDetails;

    @Column(length = 5000)
    private String floorplanManualDetails;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private YesOrNo tenancyIncludeFacilities;

    @Column(length = 5000)
    private String propertyIndoorFeatures;

    @Column(length = 5000)
    private String otherFacilitiesDetails;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private YesOrNo sharePropertyWithLandlord;

    @Column(length = 5000)
    private String sharePropertyWithLandlordDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_application_id")
    @JsonBackReference
    private CaseApplicationEntity caseApplication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pt_case_id")
    @JsonBackReference
    private PTCaseEntity ptCase;
}
