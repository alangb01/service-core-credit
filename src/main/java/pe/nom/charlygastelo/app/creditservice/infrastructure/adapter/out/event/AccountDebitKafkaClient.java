package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.domain.port.AccountDebitEventPort;
import pe.nom.charlygastelo.app.shared.avro.dto.AccountDebitRequestEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountDebitKafkaClient implements AccountDebitEventPort {

    private final AccountDebitRequestProducer requestProducer;
    private final AccountDebitResponseRegistry responseRegistry;

    @Override
    public Single<Boolean> debitAccount(
            String transactionId,
            String customerId,
            String accountId,
            BigDecimal amount,
            String description) {

        String correlationId = UUID.randomUUID().toString();

        AccountDebitRequestEvent event =
                AccountDebitRequestEvent.newBuilder()
                        .setEventId(UUID.randomUUID().toString())
                        .setEventType("ACCOUNT_DEBIT_REQUEST")
                        .setOccurredAt(Instant.now().toString())
                        .setVersion("1.0")
                        .setSource("credit-service")
                        .setCorrelationId(correlationId)
                        .setTransactionId(transactionId)
                        .setCustomerId(customerId)
                        .setAccountId(accountId)
                        .setAmount(amount.doubleValue())
                        .setDescription(description == null ? "" : description)
                        .build();

        log.info(
                "Requesting account debit. transactionId={}, customerId={}, accountId={}, amount={}, correlationId={}",
                transactionId,
                customerId,
                accountId,
                amount,
                correlationId
        );

        return responseRegistry.waitForResponse(correlationId)
                .doOnSubscribe(disposable ->
                        requestProducer.send(correlationId, event)
                )
                .doOnSuccess(success ->
                        log.info(
                                "Account debit response received. transactionId={}, correlationId={}, success={}",
                                transactionId,
                                correlationId,
                                success
                        )
                )
                .doOnError(error ->
                        log.error(
                                "Error waiting account debit response. transactionId={}, correlationId={}, reason={}",
                                transactionId,
                                correlationId,
                                error.getMessage(),
                                error
                        )
                );
    }
}