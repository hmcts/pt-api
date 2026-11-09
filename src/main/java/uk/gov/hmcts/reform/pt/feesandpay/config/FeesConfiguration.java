package uk.gov.hmcts.reform.pt.feesandpay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.pt.feesandpay.model.FeeType;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties("fees")
public class FeesConfiguration {

    private Map<FeeType, LookUpReferenceData> lookup = new EnumMap<>(FeeType.class);

    @Data
    public static class LookUpReferenceData {
        private String service;
        private String jurisdiction1;
        private String jurisdiction2;
        private String channel;
        private String event;
        private String applicantType;
        private BigDecimal amountOrVolume;
        private String keyword;
    }

    public LookUpReferenceData getLookup(FeeType feeType) {
        return lookup.get(feeType);
    }
}
