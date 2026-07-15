package uk.gov.hmcts.reform.pt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import uk.gov.hmcts.reform.idam.client.IdamApi;
import uk.gov.hmcts.reform.pt.idam.IdamUserInfoApi;

@EnableScheduling
@ImportAutoConfiguration({FeignAutoConfiguration.class, FeignClientsConfiguration.class,
    HttpMessageConvertersAutoConfiguration.class})
@EnableFeignClients(
    clients = {
        IdamUserInfoApi.class,
        IdamApi.class, // not used by pt-api code; required so ccd-sdk's IdamClient can wire.
    }
)
@SpringBootApplication(
    scanBasePackages = {
        "uk.gov.hmcts.reform.pt",
        "uk.gov.hmcts.ccd.sdk",
        "uk.gov.hmcts.reform.ccd.client"
    })
@SuppressWarnings("HideUtilityClassConstructor") // Spring needs a constructor, its not a utility class
public class Application {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
