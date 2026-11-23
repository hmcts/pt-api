package uk.gov.hmcts.reform.pt.util;

import lombok.experimental.UtilityClass;
import uk.gov.hmcts.reform.pt.entity.CasePartyEntity;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class CaseFormatter {
    public static String formatCaseName(CasePartyEntity applicant, CasePartyEntity respondent) {
        if (applicant == null || respondent == null) {
            return "";
        }

        String applicantName = getFullName(applicant::getFirstName, applicant::getLastName);
        String respondentName = getFullName(respondent::getFirstName, respondent::getLastName);
        if (respondentName.isBlank()) {
            respondentName = respondent.getOrganisationName();
        }

        return "%s vs %s".formatted(applicantName, respondentName);
    }

    private static String getFullName(Supplier<String> firstNameSupplier, Supplier<String> lastNameSupplier) {
        return Stream.of(firstNameSupplier, lastNameSupplier)
            .filter(Objects::nonNull)
            .map(Supplier::get)
            .filter(Objects::nonNull)
            .filter(name -> !name.isBlank())
            .collect(Collectors.joining(" "));
    }
}
