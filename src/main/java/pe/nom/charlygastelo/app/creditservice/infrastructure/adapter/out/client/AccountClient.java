package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.client.dto.AccountResponse;
import reactor.core.publisher.Mono;

@Component
public class AccountClient {

    private final WebClient webClient;

    public AccountClient(WebClient.Builder builder,
                         @Value("${client.account-service.base-url}") String baseUrl) {
        System.out.println(baseUrl + this.getClass().getName());
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public Mono<AccountResponse> getAccount(String accountId) {
        System.out.println("accoundID " + accountId);
        return webClient.get()
                .uri("/api/accounts/{id}", accountId)
                .retrieve()
                .bodyToMono(AccountResponse.class);
    }
}
