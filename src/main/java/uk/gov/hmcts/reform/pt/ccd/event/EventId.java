package uk.gov.hmcts.reform.pt.ccd.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventId {
    // Test Events
    CREATE_TEST_CASE("create-test-case", "Create Test Case"),
    CHANGE_TEST_CASE_STATE("change-test-case-state", "Change Test Case State"),

    // Citizen Events
    CITIZEN_CREATE_APPLICATION("citizen-create-application", "Citizen Create Application"),
    CITIZEN_UPDATE_APPLICATION("citizen-update-application", "Citizen Update Application"),
    CITIZEN_SUBMIT_APPLICATION("citizen-submit-application", "Citizen Submit Application"),

    // Fee and Pay Events
    FEE_PAYMENT_SUCCESSFUL("fee-payment-successful", "Fee & Pay: Payment successful"),
    FEE_PAYMENT_FAILED("fee-payment-failed", "Fee & Pay: Payment failed"),
    REQUEST_TOP_UP_FEE("request-top-up-fee", "Fee & Pay: Request top-up fee"),
    HWF_UPDATED_NUMBER("hwf-updated-number", "HwF: Updated number"),
    HWF_INVALID_REFERENCE("hwf-invalid-reference", "HwF: Invalid reference"),
    HWF_MORE_INFORMATION_REQUIRED("hwf-more-information-required", "HwF: More information required"),
    HWF_FULL_REMISSION("hwf-full-remission", "HwF: Full remission"),
    HWF_PART_REMISSION("hwf-part-remission", "HwF: Part remission"),
    HWF_NO_REMISSION("hwf-no-remission", "HwF: No remission");

    private final String id;
    private final String name;
}
