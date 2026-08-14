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
    CASE_ADD_LISTING_REQUIREMENTS("case-add-listing-requirements", "Case: Add listing requirements"),
    CASE_ISSUE_TO_RESPONDENT("case-issue-to-respondent", "Case: Issue to respondent"),
    STAYS_CREATE_STAY("stays-create-stay", "Stays: Create stay"),
    STAYS_EDIT_STAY("stays-edit-stay", "Stays: Edit stay"),
    CASE_CLEAR_LISTING_REQS("case-clear-listing-reqs", "Case: Clear listing reqs"),
    MANAGE_CONTACT_INFORMATION("manage-contact-information", "Manage contact information"),
    CASE_EDIT_LISTING_REQS("case-edit-listing-reqs", "Case: Edit listing reqs"),
    HEARING_CREATE_LISTING("hearing-create-listing", "Hearing: Create listing"),
    BUNDLE_CREATE_A_BUNDLE("bundle-create-a-bundle", "Bundle: Create a bundle"),
    BUNDLE_EDIT_A_BUNDLE("bundle-edit-a-bundle", "Bundle: Edit a bundle"),
    BUNDLE_STITCH_A_BUNDLE("bundle-stitch-a-bundle", "Bundle: Stitch a bundle"),
    DECISION_ISSUE_FINAL_DECISION("decision-issue-final-decision", "Decision: Issue final decision"),
    DECISION_ISSUE_A_DECISION("decision-issue-a-decision", "Decision: Issue a decision"),
    HEARINGS_EDIT_SUMMARY("hearings-edit-summary", "Hearings: Edit summary"),
    CASE_REINSTATE_CASE("case-reinstate-case", "Case: Reinstate case"),
    STAYS_REMOVE_STAY("stays-remove-stay", "Stays: Remove stay"),
    HEARINGS_CANCEL_HEARING("hearings-cancel-hearing", "Hearings: Cancel hearing"),
    HEARINGS_CREATE_SUMMARY("hearings-create-summary", "Hearings: Create summary"),
    HEARINGS_EDIT_HEARING("hearings-edit-hearing", "Hearings: Edit hearing"),
    HEARINGS_POSTPONE_HEARING("hearings-postpone-hearing", "Hearings: Postpone hearing"),
    DOCUMENT_MANAGEMENT_AMEND("document-management-amend", "Document Management: Amend"),
    DOCUMENT_MANAGEMENT_REMOVE("document-management-remove", "Document Management: Remove"),
    DOCUMENT_MANAGEMENT_UPLOAD("document-management-upload", "Document Management: Upload"),
    CASE_ADD_REVIEW_DATE("case-add-review-date", "Case: Add review date"),
    CASE_CLOSE_CASE("case-close-case", "Case: Close case"),
    CASE_EDIT_CASE("case-edit-case", "Case: Edit case"),
    CASE_SELECT_HEARING_OPTIONS("case-select-hearing-options", "Case: Select hearing options"),
    CASE_COMPLETE_VETTING("case-complete-vetting", "Case: Complete vetting"),
    CASE_REQUEST_VETTING_INFORMATION("case-request-vetting-info", "Case: Request vetting info"),
    CASE_EDIT_HEARING_OPTIONS("case-edit-hearing-options", "Case: Edit hearing options"),
    CASE_SWITCH_TO_MANUAL_MODE("case-switch-to-manual-mode", "Case: Switch to manual mode"),
    CASE_EXIT_MANUAL_MODE("case-exit-manual-mode", "Case: Exit manual mode"),

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
