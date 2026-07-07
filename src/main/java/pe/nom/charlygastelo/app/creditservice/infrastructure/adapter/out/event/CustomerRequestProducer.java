package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.event;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.CustomerRequestEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerRequestProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AvroJsonSerializer avroJsonSerializer;

    @Value("${topic.customer-request}")
    private String customerRequestTopic;

    public void send(String correlationId, CustomerRequestEvent event) {
        try {
            String payload = avroJsonSerializer.serialize(event);

            log.info("[CustomerRequestProducer] Preparing to send event. topic={}, correlationId={}, payload={}",
                    customerRequestTopic, correlationId, payload);

            kafkaTemplate.send(customerRequestTopic, correlationId, payload)
                    .whenComplete((result, error) -> {
                        if (error != null) {
                            log.error("[CustomerRequestProducer] Error sending event. topic={}, correlationId={}, " +
                                            "error={}",
                                    customerRequestTopic, correlationId, error.getMessage(), error);
                        }
                        else {
                            log.info("[CustomerRequestProducer] Event sent successfully. topic={}, correlationId={}, " +
                                            "partition={}, offset={}",
                                    customerRequestTopic,
                                    correlationId,
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        }
                    });

        }
        catch (Exception e) {
            log.error("[CustomerRequestProducer] Error serializing CustomerRequestEvent. correlationId={}, error={}",
                    correlationId, e.getMessage(), e);
        }
    }
}
