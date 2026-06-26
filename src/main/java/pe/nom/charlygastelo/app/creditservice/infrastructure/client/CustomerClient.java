package pe.nom.charlygastelo.app.creditservice.infrastructure.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import pe.nom.charlygastelo.app.creditservice.infrastructure.client.dto.CustomerResponse;
import reactor.core.publisher.Mono;

/**
 * Cliente HTTP reactivo para comunicarse con customer-service.
 */
@Component
public class CustomerClient {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerClient.class);

    private final WebClient webClient;

    public CustomerClient(WebClient.Builder builder,
                          @Value("${client.customer-service.base-url}") String baseUrl) {
        LOG.info("Initializing CustomerClient with base url {}", baseUrl);
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    /**
     * Obtiene un cliente por identificador.
     */
    public Mono<CustomerResponse> getCustomer(String customerId) {
        LOG.debug("Getting customer with id {}", customerId);
        return webClient.get()
                .uri("/api/customers/{id}", customerId)
                .retrieve()
                .bodyToMono(CustomerResponse.class);
    }
}