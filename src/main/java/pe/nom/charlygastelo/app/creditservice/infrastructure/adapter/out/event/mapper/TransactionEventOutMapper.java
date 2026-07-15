package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.event.mapper;

import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;
import pe.nom.charlygastelo.app.shared.avro.dto.TransactionFailedEvent;


@Component
public class TransactionEventOutMapper {

    public TransactionFailedEvent toFailedEvent(String transactionId, String customerId, String reason) {
        return TransactionFailedEvent.newBuilder()
                        .setEventId(UUID.randomUUID().toString())
                        .setEventType("TRANSACTION_FAILED")
                        .setOccurredAt(Instant.now().toString())
                        .setVersion("1.0")
                        .setSource("credit-service")

                        .setTransactionId(transactionId)
                        .setCustomerId(customerId)
                        .setReason(reason == null ? "" : reason)

                        .build();
    }
}
