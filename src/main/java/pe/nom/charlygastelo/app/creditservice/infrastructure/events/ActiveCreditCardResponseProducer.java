package pe.nom.charlygastelo.app.creditservice.infrastructure.events;

import java.time.Instant;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import pe.nom.charlygastelo.app.shared.avro.dto.ActiveCreditCardResponseEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActiveCreditCardResponseProducer {

    private final KafkaTemplate<String,String> kafkaTemplate;
    private final AvroJsonSerializer serializer;

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
                    serializer.serialize(event)
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