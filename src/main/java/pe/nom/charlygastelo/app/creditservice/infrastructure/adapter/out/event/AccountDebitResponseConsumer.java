package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.AccountDebitResponseEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountDebitResponseConsumer {

    private final AvroJsonDeserializer deserializer;
    private final AccountDebitResponseRegistry registry;

    @KafkaListener(
            topics = "${topic.account-debit-response}",
            groupId = "credit-service")
    public void consume(String message) {
        log.debug("AccountDebitResponseEvent raw message received");

        try {
            AccountDebitResponseEvent event =
                    deserializer.deserialize(
                            message,
                            AccountDebitResponseEvent.class,
                            AccountDebitResponseEvent.getClassSchema()
                    );

            log.info(
                    "AccountDebitResponseEvent received. correlationId={}, transactionId={}, success={}, reason={}",
                    event.getCorrelationId(),
                    event.getTransactionId(),
                    event.getSuccess(),
                    event.getReason()
            );

            registry.complete(event);

        }
        catch (Exception e) {
            log.error(
                    "Error processing AccountDebitResponseEvent. reason={}",
                    e.getMessage(),
                    e
            );
        }
    }
}