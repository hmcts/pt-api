package uk.gov.hmcts.reform.pt.util;

import lombok.experimental.UtilityClass;
import uk.gov.hmcts.reform.pt.ccd.domain.PTCase;
import uk.gov.hmcts.reform.pt.ccd.domain.PartyDetails;

import java.util.function.Supplier;

@UtilityClass
public class CaseFormatter {
    public static String formatCaseName(PTCase ptCase) {
        String applicantName = getFullName(ptCase::getApplicantFirstName, ptCase::getApplicantLastName);

        PartyDetails landlordDetails = ptCase.getLandlordDetails().getLandlordPartyDetails();
        String respondentName = getFullName(landlordDetails::getFirstName, landlordDetails::getLastName);
        if (respondentName.isBlank()) {
            respondentName = landlordDetails.getOrganisationName();
        }

        return "%s vs %s".formatted(applicantName, respondentName);
    }

    private static String getFullName(Supplier<String> firstNameSupplier, Supplier<String> lastNameSupplier) {
        return "%s %s".formatted(firstNameSupplier.get(), lastNameSupplier.get());
    }
}
