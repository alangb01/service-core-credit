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

            kafkaTemplate.send(overdueDebtResponseTopic, correlationId, payload)
                    .whenComplete((result, error) -> {
                        if (error != null) {
                            log.error("Error publishing OverdueDebtResponseEvent", error);
                        }
                        else {
                            log.info("OverdueDebtResponseEvent published. correlationId={}", correlationId);
                        }
                    });

        }
        catch (Exception e) {
            log.error("Error serializing OverdueDebtResponseEvent", e);
        }
    }
}