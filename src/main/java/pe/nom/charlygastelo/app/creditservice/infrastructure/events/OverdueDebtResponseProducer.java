package pe.nom.charlygastelo.app.creditservice.infrastructure.events;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.OverdueDebtResponseEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class OverdueDebtResponseProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AvroJsonSerializer avroJsonSerializer;

    @Value("${topic.overdue-debt-response}")
    private String overdueDebtResponseTopic;

    public void publish(String correlationId, OverdueDebtResponseEvent event) {
        try {
            String payload = avroJsonSerializer.serialize(event);

            log.info("[OverdueDebtResponseProducer] Preparing to publish event. topic={}, correlationId={}, payload={}",
                    overdueDebtResponseTopic, correlationId, payload);

            kafkaTemplate.send(overdueDebtResponseTopic, correlationId, payload)
                    .whenComplete((result, error) -> {
                        if (error != null) {
                            log.error("[OverdueDebtResponseProducer] Error publishing event. topic={}, correlationId={}, error={}",
                                    overdueDebtResponseTopic, correlationId, error.getMessage(), error);
                        } else {
                            log.info("[OverdueDebtResponseProducer] Event published successfully. topic={}, correlationId={}, partition={}, offset={}",
                                    overdueDebtResponseTopic,
                                    correlationId,
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        }
                    });

        } catch (Exception e) {
            log.error("[OverdueDebtResponseProducer] Error serializing OverdueDebtResponseEvent. correlationId={}, error={}",
                    correlationId, e.getMessage(), e);
        }
    }
}
