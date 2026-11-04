package uk.gov.hmcts.reform.pt.feesandpay.model;

import lombok.Getter;

@Getter
public enum FeeType {
    //TODO: update once we receive fee ref data
    APPLICATION_FEE("applicationFee");

    private final String code;

    FeeType(String code) {
        this.code = code;
    }
}

