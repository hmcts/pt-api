package uk.gov.hmcts.reform.pt.ccd.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventId {
    CITIZEN_CREATE_APPLICATION("citizen-create-application", "Citizen Create Application"),
    CITIZEN_UPDATE_APPLICATION("citizen-update-application", "Citizen Update Application"),
    CITIZEN_SUBMIT_APPLICATION("citizen-submit-application", "Citizen Submit Application"),
    CREATE_DRAFT_CASE("create-draft-case", "Case: Create Draft Case"),
    DELETE_DRAFT_CASE("delete-draft-case", "Case: Delete Draft Case"),
    SYSTEM_DISCARD_DRAFT_CASE("system-discard-draft-case", "Case: Discard Draft Case"),
    RESUME_DRAFT_CASE("resume-draft-case", "Case: Resume Draft Case"),
    SUBMIT_DRAFT_CASE("submit-draft-case", "Case: Submit Draft Case"),
    EDIT_DRAFT_CASE("edit-draft-case", "Case: Edit Draft Case"),
    CASE_ADD_HEARING_OPTIONS("case-add-hearing-options", "Case: Add hearing options"),
    SYSTEM_GENERATE_CASE_FORM("system-generate-case-form", "Generate case form"),
    FLAGS_CREATE_FLAG("flags-create-flag", "Flags: Create flag"),
    FLAGS_MANAGE_FLAG("flags-manage-flag", "Flags: Manage flag"),
    MANAGE_PARTY_INFORMATION("manage-party-information", "Manage party information"),

    // TEST EVENTS

    CREATE_TEST_CASE("create-test-case", "Create Test Case"),
    CHANGE_TEST_CASE_STATE("change-test-case-state", "Change Test Case State");

    private final String id;
    private final String name;
}
