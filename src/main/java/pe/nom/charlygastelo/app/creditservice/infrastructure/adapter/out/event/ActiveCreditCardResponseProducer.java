package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.event;

import java.time.Instant;
import java.util.UUID;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.ActiveCreditCardResponseEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActiveCreditCardResponseProducer {

    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;

    @Value("${topic.active-credit-card-response}")
    private String topic;

    public void publish(
            String correlationId,
            String customerId,
            boolean hasCard) {

        ActiveCreditCardResponseEvent event =
                ActiveCreditCardResponseEvent.newBuilder()
                        .setEventId(UUID.randomUUID().toString())
                        .setEventType("ACTIVE_CREDIT_CARD_RESPONSE")
                        .setOccurredAt(Instant.now().toString())
                        .setVersion("1.0")
                        .setSource("credit-service")
                        .setCorrelationId(correlationId)
                        .setCustomerId(customerId)
                        .setHasActiveCreditCard(hasCard)
                        .build();

        try {

            kafkaTemplate.send(
                    topic,
                    correlationId,
                    event
            );

            log.info(
                    "ActiveCreditCardResponseEvent published. correlationId={}, customerId={}, hasCard={}",
                    correlationId,
                    customerId,
                    hasCard
            );

        } catch (Exception e) {

            log.error(
                    "Error publishing ActiveCreditCardResponseEvent",
                    e
            );
        }
    }
}