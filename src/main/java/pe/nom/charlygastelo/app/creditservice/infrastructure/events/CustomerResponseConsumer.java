package pe.nom.charlygastelo.app.creditservice.infrastructure.events;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.CustomerResponseEvent;
@RequiredArgsConstructor
@Slf4j
@Component
public class CustomerResponseConsumer {

    private final AvroJsonDeserializer deserializer;
    private final CustomerResponseRegistry registry;

    @KafkaListener(
            topics = "${topic.customer-response}",
            groupId = "credit-service")
    public void consumeCustomerResponse(String message) {

        log.info(
                "CustomerResponseEvent received."
        );

        try {

            CustomerResponseEvent event =
                    deserializer.deserialize(
                            message,
                            CustomerResponseEvent.class,
                            CustomerResponseEvent.getClassSchema()
                    );

            log.info(
                    "CustomerResponseEvent parsed successfully. correlationId={}, customerId={}, found={}",
                    event.getCorrelationId(),
                    event.getCustomerId(),
                    event.getFound()
            );

            registry.complete(event);

        }
        catch (Exception e) {

            log.error(
                    "Error processing CustomerResponseEvent",
                    e
            );

        }

    }

}