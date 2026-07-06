import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "s2s_auth", port = "5050")
public class PactFrameworkSmokeTest {

    @Pact(consumer = "pt-api")
    public V4Pact simplePact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return builder
            .given("service is healthy")
            .uponReceiving("a simple health check request")
            .path("/health")
            .method("GET")
            .willRespondWith()
            .status(200)
            .headers(headers)
            .body("{\"status\":\"UP\"}")
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "simplePact")
    void verifyPact(MockServer mockServer) throws IOException, InterruptedException {
        String url = mockServer.getUrl() + "/health";

        String response = java.net.http.HttpClient.newHttpClient()
            .send(
                java.net.http.HttpRequest.newBuilder(java.net.URI.create(url)).GET().build(),
                java.net.http.HttpResponse.BodyHandlers.ofString()
            )
            .body();

        assertEquals("{\"status\":\"UP\"}", response);
    }
}