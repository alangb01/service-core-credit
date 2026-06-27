package pe.nom.charlygastelo.app.creditservice.infrastructure.events;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.CustomerResponseEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerResponseConsumer {

    private final ObjectMapper objectMapper;
    private final CustomerResponseRegistry registry;

    @KafkaListener(topics = "${topic.customer-response}", groupId = "credit-service")
    public void consumeCustomerResponse(String message) {

        log.info("[CustomerResponseConsumer] Received raw message: {}", message);

        try {
            CustomerResponseEvent event =
                    objectMapper.readValue(message, CustomerResponseEvent.class);

            log.info("[CustomerResponseConsumer] Parsed CustomerResponseEvent. correlationId={}, customerId={}, status={}",
                    event.getCorrelationId(),
                    event.getCustomerId(),
                    event.getActive());

            registry.complete(event);

            log.info("[CustomerResponseConsumer] Registry completed for correlationId={}",
                    event.getCorrelationId());

        } catch (Exception e) {
            log.error("[CustomerResponseConsumer] Error processing CustomerResponseEvent. rawMessage={}, error={}",
                    message, e.getMessage(), e);
        }
    }
}
