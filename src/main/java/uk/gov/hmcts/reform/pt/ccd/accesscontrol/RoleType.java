package uk.gov.hmcts.reform.pt.ccd.accesscontrol;

public enum RoleType {

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
