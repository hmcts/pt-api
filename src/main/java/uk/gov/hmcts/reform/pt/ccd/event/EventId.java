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
    SYSTEM_DELETE_AS_PER_HMCTS_POLICY("system-delete-as-per-hmcts-policy", "Delete as per HMCTS policy"),
    LINKS_LINK_CASE("links-link-case", "Links: Link case"),
    LINKS_MANAGE_LINKS("links-manage-links", "Links: Manage links"),
    REFER_TO_CASE_OFFICER("refer-to-case-officer", "Refer to case officer"),
    REFER_TO_JUDGE("refer-to-judge", "Refer to judge"),
    REFER_TO_LEGAL_OFFICER("refer-to-legal-officer", "Refer to legal officer"),
    REFER_TO_TEAM_LEADER("refer-to-team-leader", "Refer to team leader"),
    CASE_ADD_NOTE("case-add-note", "Case: Add note"),
    CASE_CHANGE_SECURITY_CLASS("case-change-security-class", "Case: Change security class"),
    CASE_CONTACT_PARTIES("case-contact-parties", "Case: Contact Parties"),
    CASE_SUBMIT_FURTHER_INFO("case-submit-further-info", "Case: Submit further info"),
    NOTICE_OF_CHANGE("notice-of-change", "Notice of change"),
    ORDERS_CREATE_DRAFT_ORDER("orders-create-draft-order", "Orders: Create draft order"),
    ORDERS_EDIT_DRAFT_ORDER("orders-edit-draft-order", "Orders: Edit draft order"),
    ORDERS_ISSUE_DRAFT_ORDER("orders-issue-draft-order", "Orders: Issue draft order"),
    RAISE_A_QUERY("raise-a-query", "Raise a query"),
    RESET_PIN("reset-pin", "Reset PIN"),

    // TEST EVENTS

    CREATE_TEST_CASE("create-test-case", "Create Test Case"),
    CHANGE_TEST_CASE_STATE("change-test-case-state", "Change Test Case State");

    private final String id;
    private final String name;
}
