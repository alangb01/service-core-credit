package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.client;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.client.dto.CreateCardConsumptionRequest;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.client.dto.CreateCreditPaymentRequest;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.client.dto.CreateDisbursementRequest;
import reactor.core.publisher.Mono;

/**
 * Cliente HTTP reactivo para comunicarse con transaction-service.
 */
@Component
public class TransactionClient {

    private final WebClient webClient;

    public TransactionClient(WebClient.Builder builder,
                             @Value("${client.transaction-service.base-url}") String baseUrl) {

        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public Mono<Void> createDisbursement(String creditId,
                                         String customerId,
                                         String accountId,
                                         BigDecimal amount) {

        CreateDisbursementRequest request = new CreateDisbursementRequest(
                creditId,
                customerId,
                accountId,
                amount
        );

        return webClient.post()
                .uri("/api/transactions/disbursement")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class);
    }

    /**
     * Registra un pago de crédito o tarjeta en transaction-service.
     */
    public Mono<Void> createCreditPayment(String creditId,
                                          String customerId,
                                          BigDecimal amount) {

        CreateCreditPaymentRequest request = new CreateCreditPaymentRequest(
                creditId,
                customerId,
                amount
        );

        return webClient.post()
                .uri("/api/transactions/credit-payment")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class);
    }

    /**
     * Registra un consumo de tarjeta en transaction-service.
     */
    public Mono<Void> createCardConsumption(String creditId,
                                            String customerId,
                                            BigDecimal amount,
                                            String description) {

        CreateCardConsumptionRequest request = new CreateCardConsumptionRequest(
                creditId,
                customerId,
                amount,
                description
        );

        return webClient.post()
                .uri("/api/transactions/card-consumption")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class);
    }
}