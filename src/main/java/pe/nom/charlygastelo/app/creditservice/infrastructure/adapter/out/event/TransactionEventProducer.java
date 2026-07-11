package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.event;

import java.time.Instant;
import java.util.UUID;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Completable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.domain.port.TransactionEventPort;
import pe.nom.charlygastelo.app.shared.avro.dto.TransactionCompletedEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.TransactionCreatedEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.TransactionFailedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionEventProducer implements TransactionEventPort {

    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;

    @Value("${topic.transaction-completed}")
    private String transactionCompletedTopic;

    @Value("${topic.transaction-failed}")
    private String transactionFailedTopic;

    @Override
    public Completable publishTransactionCompleted(TransactionCreatedEvent event) {

        TransactionCompletedEvent completedEvent =
                TransactionCompletedEvent.newBuilder()
                        .setEventId(UUID.randomUUID().toString())
                        .setEventType("TRANSACTION_COMPLETED")
                        .setOccurredAt(Instant.now().toString())
                        .setVersion("1.0")
                        .setSource("credit-service")
                        .setTransactionId(event.getTransactionId().toString())
                        .setCustomerId(event.getCustomerId().toString())
                        .setStatus("COMPLETED")
                        .setAmount(event.getAmount())
                        .build();

        return publish(
                transactionCompletedTopic,
                event.getTransactionId().toString(),
                completedEvent
        );
    }

    @Override
    public Completable publishTransactionFailed(
            TransactionCreatedEvent event,
            String reason) {

        TransactionFailedEvent failedEvent =
                TransactionFailedEvent.newBuilder()
                        .setEventId(UUID.randomUUID().toString())
                        .setEventType("TRANSACTION_FAILED")
                        .setOccurredAt(Instant.now().toString())
                        .setVersion("1.0")
                        .setSource("credit-service")
                        .setTransactionId(event.getTransactionId().toString())
                        .setCustomerId(event.getCustomerId().toString())
                        .setReason(reason == null ? "" : reason)
                        .build();

        return publish(
                transactionFailedTopic,
                event.getTransactionId().toString(),
                failedEvent
        );
    }

    private Completable publish(
            String topic,
            String key,
            SpecificRecordBase event) {

        return Completable.create(emitter -> {
            try {

                kafkaTemplate.send(topic, key, event)
                        .whenComplete((result, error) -> {
                            if (error != null) {
                                log.error(
                                        "Error publishing transaction event. topic={}, key={}, eventClass={}, reason={}",
                                        topic,
                                        key,
                                        event.getClass().getSimpleName(),
                                        error.getMessage(),
                                        error
                                );
                                emitter.onError(error);
                                return;
                            }

                            log.info(
                                    "Transaction event published successfully. topic={}, key={}, eventClass={}, partition={}, offset={}",
                                    topic,
                                    key,
                                    event.getClass().getSimpleName(),
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset()
                            );

                            emitter.onComplete();
                        });

            }
            catch (Exception e) {
                log.error(
                        "Error serializing transaction event. topic={}, key={}, eventClass={}, reason={}",
                        topic,
                        key,
                        event.getClass().getSimpleName(),
                        e.getMessage(),
                        e
                );
                emitter.onError(e);
            }
        });
    }
}