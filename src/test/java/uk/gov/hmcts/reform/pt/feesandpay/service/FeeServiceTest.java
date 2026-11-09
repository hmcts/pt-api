package uk.gov.hmcts.reform.pt.feesandpay.service;

import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.fees.client.model.FeeLookupResponseDto;
import uk.gov.hmcts.reform.pt.feesandpay.client.PTFeesClient;
import uk.gov.hmcts.reform.pt.feesandpay.exception.FeeNotFoundException;
import uk.gov.hmcts.reform.pt.feesandpay.model.FeeDetails;
import uk.gov.hmcts.reform.pt.feesandpay.model.FeeType;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeeServiceTest {

    @Mock
    private PTFeesClient ptFeesClient;

    @InjectMocks
    private FeeService feeService;

    @Test
    void shouldMapFeeLookupResponseToFeeDetails() {
        FeeLookupResponseDto response = FeeLookupResponseDto.builder()
            .code("FEE0001")
            .description("Application fee")
            .feeAmount(new BigDecimal("100.00"))
            .version(3)
            .build();
        when(ptFeesClient.lookupFee(FeeType.APPLICATION_FEE, null)).thenReturn(response);

        FeeDetails result = feeService.getFee(FeeType.APPLICATION_FEE);

        assertThat(result.getCode()).isEqualTo("FEE0001");
        assertThat(result.getDescription()).isEqualTo("Application fee");
        assertThat(result.getFeeAmount()).isEqualByComparingTo("100.00");
        assertThat(result.getVersion()).isEqualTo(3);
    }

    @Test
    void shouldPassAmountOrVolumeToClient() {
        BigDecimal amountOrVolume = new BigDecimal("2500.00");
        when(ptFeesClient.lookupFee(FeeType.APPLICATION_FEE, amountOrVolume))
            .thenReturn(FeeLookupResponseDto.builder().code("FEE0002").build());

        FeeDetails result = feeService.getFee(FeeType.APPLICATION_FEE, amountOrVolume);

        assertThat(result.getCode()).isEqualTo("FEE0002");
    }

    @Test
    void shouldWrapFeignExceptionInFeeNotFoundException() {
        FeignException feignException = mock(FeignException.class);
        when(ptFeesClient.lookupFee(FeeType.APPLICATION_FEE, null)).thenThrow(feignException);

        assertThatThrownBy(() -> feeService.getFee(FeeType.APPLICATION_FEE))
            .isInstanceOf(FeeNotFoundException.class)
            .hasCause(feignException);
    }

    @Test
    void shouldPropagateFeeNotFoundExceptionFromClient() {
        FeeNotFoundException exception = new FeeNotFoundException("not configured");
        when(ptFeesClient.lookupFee(FeeType.APPLICATION_FEE, null)).thenThrow(exception);

        assertThatThrownBy(() -> feeService.getFee(FeeType.APPLICATION_FEE)).isSameAs(exception);
    }
}
