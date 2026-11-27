package uk.gov.hmcts.reform.pt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractAddressEntity extends AuditableEntity {
    @Column(name = "address_line_1", length = 100, nullable = false)
    private String addressLine1;

    @Column(name = "address_line_2", length = 100)
    private String addressLine2;

    @Column(name = "address_line_3", length = 100)
    private String addressLine3;

    @Column(length = 100, nullable = false)
    private String townCity;

    @Column(length = 100)
    private String county;

    @Column(name = "post_code", length = 16, nullable = false)
    private String postcode;

    @Column(length = 100)
    private String country;
}
