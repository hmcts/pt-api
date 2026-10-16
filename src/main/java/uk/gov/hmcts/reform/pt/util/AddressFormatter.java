package uk.gov.hmcts.reform.pt.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import uk.gov.hmcts.reform.pt.ccd.domain.PropertyDetails;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class AddressFormatter {
    public static final String COMMA_DELIMITER = ", ";

    public static String formatAddressWithoutCountry(PropertyDetails address, String delimiter) {
        return Stream.of(
                address.getAddressLine1(),
                address.getAddressLine2(),
                address.getPostTown(),
                address.getCounty(),
                address.getPostcode())
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(delimiter));
    }
}
