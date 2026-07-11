package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.AccountDebitResponseEvent;

@Slf4j
@Component
public class AccountDebitResponseRegistry {

    @Value("${account-debit.response.timeout:2}")
    private int timeout;

    private final Map<String, SingleEmitter<Boolean>> pendingRequests =
            new ConcurrentHashMap<>();

    public Single<Boolean> waitForResponse(String correlationId) {
        log.info("Waiting AccountDebitResponseEvent. correlationId={}, timeout={}s",
                correlationId,
                timeout);

        return Single.<Boolean>create(emitter ->
                        pendingRequests.put(correlationId, emitter)
                )
                .timeout(timeout, TimeUnit.SECONDS)
                .doFinally(() -> {
                    pendingRequests.remove(correlationId);
                    log.debug("Account debit pending request removed. correlationId={}",
                            correlationId);
                });
    }

    public void complete(AccountDebitResponseEvent event) {
        String correlationId = event.getCorrelationId().toString();

        SingleEmitter<Boolean> emitter =
                pendingRequests.remove(correlationId);

        if (emitter == null) {
            log.warn("No pending account debit request found. correlationId={}",
                    correlationId);
            return;
        }

        if (!event.getSuccess()) {
            log.warn(
                    "Account debit failed. correlationId={}, transactionId={}, reason={}",
                    correlationId,
                    event.getTransactionId(),
                    event.getReason()
            );

            emitter.onError(
                    new IllegalStateException(event.getReason().toString())
            );
            return;
        }

        log.info(
                "Account debit completed successfully. correlationId={}, transactionId={}, balanceAfter={}",
                correlationId,
                event.getTransactionId(),
                event.getBalanceAfter()
        );

        emitter.onSuccess(true);
    }
}