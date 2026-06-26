package pe.nom.charlygastelo.app.creditservice.infrastructure.events;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.shared.avro.dto.CustomerRequestEvent;

@Component
@RequiredArgsConstructor
public class CustomerRequestProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AvroJsonSerializer avroJsonSerializer;

    @Value("${topic.customer-request}")
    private String customerRequestTopic;

    public void send(String correlationId, CustomerRequestEvent event) {
        String payload = avroJsonSerializer.serialize(event);
        kafkaTemplate.send(customerRequestTopic, correlationId, payload);
    }
}