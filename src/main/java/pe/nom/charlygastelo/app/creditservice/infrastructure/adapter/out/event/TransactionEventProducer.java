package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.event;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Completable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.domain.port.TransactionEventPort;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.event.mapper.TransactionEventOutMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionEventProducer implements TransactionEventPort {

    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;
    private final TransactionEventOutMapper mapper;


    @Value("${topic.transaction-failed}")
    private String transactionFailedTopic;



    @Override
    public Completable publishTransactionFailed(String transactionId, String customerId, String reason) {
        return publish(
                transactionFailedTopic,
                transactionId,
                mapper.toFailedEvent(transactionId, customerId, reason)
        );
    }

    private Completable publish(String topic, String key, SpecificRecordBase event) {
        log.info("[CREDIT-EVENT] Sending event. topic={}, key={}", topic, key);
        log.debug("[CREDIT-EVENT] Serializing event. event={}", event);
        return Completable.fromFuture(
                kafkaTemplate.send(topic, key, event)
                        .whenComplete((result, error) -> {
                            if (error != null) {
                                log.error("[CREDIT-EVENT] Error sending event. topic={}, key={}, reason={}",
                                        topic, key, error.getMessage(), error);
                            }
                            else {
                                log.info("[CREDIT-EVENT] Event sent successfully. topic={}, key={}, partition={}, offset={}",
                                        topic, key,
                                        result.getRecordMetadata().partition(),
                                        result.getRecordMetadata().offset());
                            }
                        })
        );
    }
}