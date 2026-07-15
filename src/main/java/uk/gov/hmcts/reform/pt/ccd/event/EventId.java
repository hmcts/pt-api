package uk.gov.hmcts.reform.pt.ccd.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventId {
    CREATE_TEST_CASE("create-test-case", "Create Test Case"),
    CITIZEN_CREATE_APPLICATION("citizen-create-application", "Citizen Create Application");

    private final String id;
    private final String name;
}
