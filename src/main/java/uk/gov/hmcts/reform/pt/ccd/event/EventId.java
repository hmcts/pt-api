package uk.gov.hmcts.reform.pt.ccd.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventId {
    CITIZEN_CREATE_APPLICATION("citizen-create-application", "Citizen Create Application"),
    CITIZEN_UPDATE_APPLICATION("citizen-update-application", "Citizen Update Application"),
    CITIZEN_SUBMIT_APPLICATION("citizen-submit-application", "Citizen Submit Application"),
    CREATE_DRAFT_CASE("create-draft-case", "Create Draft Case"),
    DELETE_DRAFT_CASE("delete-draft-case", "Delete Draft Case"),
    SYSTEM_DISCARD_DRAFT_CASE("system-discard-draft-case", "Discard Draft Case"),
    RESUME_DRAFT_CASE("resume-draft-case", "Resume Draft Case"),
    SUBMIT_DRAFT_CASE("submit-draft-case", "Submit Draft Case"),
    EDIT_DRAFT_CASE("edit-draft-case", "Edit Draft Case"),

    // TEST EVENTS

    CREATE_TEST_CASE("create-test-case", "Create Test Case"),
    CHANGE_TEST_CASE_STATE("change-test-case-state", "Change Test Case State");

    private final String id;
    private final String name;
}
