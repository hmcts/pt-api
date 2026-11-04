package uk.gov.hmcts.reform.pt.feesandpay.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.fees.client.model.FeeLookupResponseDto;
import uk.gov.hmcts.reform.pt.feesandpay.client.PTFeesClient;
import uk.gov.hmcts.reform.pt.feesandpay.config.FeesConfiguration;
import uk.gov.hmcts.reform.pt.feesandpay.exception.FeeNotFoundException;
import uk.gov.hmcts.reform.pt.feesandpay.model.FeeDetails;
import uk.gov.hmcts.reform.pt.feesandpay.model.FeeType;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeeService {

    private final PTFeesClient ptFeesClient;

    /**
     * Retrieves fee information from the Fees Register based on a configured fee type key.
     * The key must exist in {@link FeesConfiguration}; otherwise a {@link FeeNotFoundException} is thrown.
     *
     * @param feeType the logical fee type key (e.g., "applicationFee")
     * @return a {@link FeeDetails} representing the fee details
     * @throws FeeNotFoundException if the fee type is not configured or the Fees Register call fails
     */
    public FeeDetails getFee(FeeType feeType) {
        return getFee(feeType, null);
    }

    public FeeDetails getFee(FeeType feeType, BigDecimal amountOrVolume) {
        log.debug("Requesting fee of type: {} amountOrVolume={}", feeType, amountOrVolume);
        try {
            FeeLookupResponseDto feeLookupResponse = ptFeesClient.lookupFee(feeType, amountOrVolume);
            log.debug("Successfully retrieved fee: type={}, code={}, amount={}",
                      feeType, feeLookupResponse.getCode(), feeLookupResponse.getFeeAmount());

            return FeeDetails.fromFeeLookupResponse(feeLookupResponse);
        } catch (FeignException e) {
            log.error("Failed to retrieve fee for type: {}", feeType, e);
            throw new FeeNotFoundException("Unable to retrieve fee: " + feeType, e);
        }
    }

}
