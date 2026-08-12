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

    // Manage Case Events
    CASE_ADD_REVIEW_DATE("case-add-review-date", "Case: Add review date"),
    CASE_CLOSE_CASE("case-close-case", "Case: Close case"),
    CASE_EDIT_CASE("case-edit-case", "Case: Edit case"),
    CASE_SELECT_HEARING_OPTIONS("case-select-hearing-options", "Case: Select hearing options"),
    CASE_COMPLETE_VETTING("case-complete-vetting", "Case: Complete vetting"),
    CASE_REQUEST_VETTING_INFORMATION("case-request-vetting-info", "Case: Request vetting info"),
    CASE_EDIT_HEARING_OPTIONS("case-edit-hearing-options", "Case: Edit hearing options"),
    CASE_SWITCH_TO_MANUAL_MODE("case-switch-to-manual-mode", "Case: Switch to manual mode"),
    CASE_EXIT_MANUAL_MODE("case-exit-manual-mode", "Case: Exit manual mode");

    private final String id;
    private final String name;
}
