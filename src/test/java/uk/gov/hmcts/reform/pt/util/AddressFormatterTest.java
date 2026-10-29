package uk.gov.hmcts.reform.pt.util;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyDetails;

import static org.assertj.core.api.Assertions.assertThat;

class AddressFormatterTest {

    @Test
    void shouldFormatAddressWithAllFieldsPresent() {
        PropertyDetails propertyDetails = PropertyDetails.builder()
            .addressLine1("Flat 1")
            .addressLine2("10 High Street")
            .postTown("London")
            .county("Greater London")
            .postcode("SW1A 1AA")
            .build();

        String formattedAddress = AddressFormatter.formatAddressWithoutCountry(
            propertyDetails,
            AddressFormatter.COMMA_DELIMITER
        );

        assertThat(formattedAddress).isEqualTo("Flat 1, 10 High Street, London, Greater London, SW1A 1AA");
    }

    @Test
    void shouldIgnoreNullAndBlankFields() {
        PropertyDetails propertyDetails = PropertyDetails.builder()
            .addressLine1("10 High Street")
            .addressLine2("")
            .postTown("London")
            .county(null)
            .postcode("SW1A 1AA")
            .build();

        String formattedAddress = AddressFormatter.formatAddressWithoutCountry(
            propertyDetails,
            AddressFormatter.COMMA_DELIMITER
        );

        assertThat(formattedAddress).isEqualTo("10 High Street, London, SW1A 1AA");
    }

    @Test
    void shouldReturnEmptyStringWhenAllFieldsAreBlankOrNull() {
        PropertyDetails propertyDetails = PropertyDetails.builder()
            .addressLine1("")
            .addressLine2(null)
            .postTown("   ")
            .county(null)
            .postcode("")
            .build();

        String formattedAddress = AddressFormatter.formatAddressWithoutCountry(
            propertyDetails,
            AddressFormatter.COMMA_DELIMITER
        );

        assertThat(formattedAddress).isEmpty();
    }
}
