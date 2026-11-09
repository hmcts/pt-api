package uk.gov.hmcts.reform.pt.feesandpay.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.fees.client.FeesApi;
import uk.gov.hmcts.reform.fees.client.model.FeeLookupResponseDto;
import uk.gov.hmcts.reform.pt.feesandpay.config.FeesConfiguration;
import uk.gov.hmcts.reform.pt.feesandpay.exception.FeeNotFoundException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.pt.feesandpay.model.FeeType.APPLICATION_FEE;

@ExtendWith(MockitoExtension.class)
class PTFeesClientTest {

    @Mock
    private FeesApi feesApi;

    private FeesConfiguration feesConfiguration;

    private PTFeesClient ptFeesClient;

    @BeforeEach
    void setUp() {
        feesConfiguration = new FeesConfiguration();
        ptFeesClient = new PTFeesClient(feesConfiguration, feesApi);
    }

    @Test
    void shouldLookUpFeeUsingConfiguredReferenceData() {
        feesConfiguration.getLookup().put(APPLICATION_FEE, referenceData(BigDecimal.ONE));
        FeeLookupResponseDto response = FeeLookupResponseDto.builder().code("FEE0001").build();
        when(feesApi.lookupFee("service", "jurisdiction1", "jurisdiction2", "default", "issue",
                               "all", BigDecimal.ONE, "keyword")).thenReturn(response);

        FeeLookupResponseDto result = ptFeesClient.lookupFee(APPLICATION_FEE);

        assertThat(result).isSameAs(response);
    }

    @Test
    void shouldUseSuppliedAmountOrVolumeOverConfiguredValue() {
        feesConfiguration.getLookup().put(APPLICATION_FEE, referenceData(BigDecimal.ONE));
        BigDecimal amountOrVolume = new BigDecimal("2500.00");

        ptFeesClient.lookupFee(APPLICATION_FEE, amountOrVolume);

        verify(feesApi).lookupFee("service", "jurisdiction1", "jurisdiction2", "default", "issue",
                                  "all", amountOrVolume, "keyword");
    }

    @Test
    void shouldThrowFeeNotFoundExceptionWhenFeeTypeNotConfigured() {
        assertThatThrownBy(() -> ptFeesClient.lookupFee(APPLICATION_FEE))
            .isInstanceOf(FeeNotFoundException.class)
            .hasMessageContaining(APPLICATION_FEE.name());

        verifyNoInteractions(feesApi);
    }

    @Test
    void shouldPassNullAmountOrVolumeWhenNeitherSuppliedNorConfigured() {
        feesConfiguration.getLookup().put(APPLICATION_FEE, referenceData(null));

        ptFeesClient.lookupFee(APPLICATION_FEE);

        verify(feesApi).lookupFee(anyString(), anyString(), anyString(), anyString(), anyString(),
                                  anyString(), any(), anyString());
    }

    private static FeesConfiguration.LookUpReferenceData referenceData(BigDecimal amountOrVolume) {
        FeesConfiguration.LookUpReferenceData data = new FeesConfiguration.LookUpReferenceData();
        data.setService("service");
        data.setJurisdiction1("jurisdiction1");
        data.setJurisdiction2("jurisdiction2");
        data.setChannel("default");
        data.setEvent("issue");
        data.setApplicantType("all");
        data.setAmountOrVolume(amountOrVolume);
        data.setKeyword("keyword");
        return data;
    }
}
