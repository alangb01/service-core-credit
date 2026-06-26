package pe.nom.charlygastelo.app.creditservice.infrastructure.client;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import pe.nom.charlygastelo.app.creditservice.infrastructure.client.dto.LimitValidationRequest;
import pe.nom.charlygastelo.app.creditservice.infrastructure.client.dto.LimitValidationResponse;
import reactor.core.publisher.Mono;



@Component
public class LimitClient {

    private final WebClient webClient;

    public LimitClient(WebClient.Builder builder,
                       @Value("${client.limit-service.base-url}") String baseUrl) {
        System.out.println(baseUrl + this.getClass().getName());
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public Mono<LimitValidationResponse> validateCreditLimit(String customerId, BigDecimal amount) {
        LimitValidationRequest request = new LimitValidationRequest(customerId, amount);
        System.out.println("Limit " + request);
        return webClient.post()
                .uri("/api/limits/check")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(LimitValidationResponse.class);
    }
}
