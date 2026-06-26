package pe.nom.charlygastelo.app.creditservice.infrastructure.client;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import pe.nom.charlygastelo.app.creditservice.infrastructure.client.dto.FraudCheckRequest;
import pe.nom.charlygastelo.app.creditservice.infrastructure.client.dto.FraudCheckResponse;
import reactor.core.publisher.Mono;

@Component
public class FraudClient {

    private final WebClient webClient;

    public FraudClient(WebClient.Builder builder,
                       @Value("${client.fraud-service.base-url}") String baseUrl) {
        System.out.println(baseUrl + this.getClass().getName());
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public Mono<FraudCheckResponse> evaluateCreditFraud(String creditId,
                                                        String customerId,
                                                        BigDecimal amount) {

        FraudCheckRequest request = new FraudCheckRequest(creditId, customerId, amount);
        System.out.println("evaluate " + request);
        return webClient.post()
                .uri("/api/fraud/credit/check")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(FraudCheckResponse.class);
    }
}
