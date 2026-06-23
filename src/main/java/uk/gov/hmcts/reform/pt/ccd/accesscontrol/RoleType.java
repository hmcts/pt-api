package uk.gov.hmcts.reform.pt.ccd.accesscontrol;

enum RoleType {

    IDAM("idam:"),
    RAS("");

    private final String prefix;

    RoleType(String prefix) {
        this.prefix = prefix;
    }

    public String prefix() {
        return prefix;
    }
}
