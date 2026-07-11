package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.event;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.CreditResponseEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreditResponseProducer {

    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;

    @Value("${topic.credit-response}")
    private String creditResponseTopic;

    public void publish(String correlationId, CreditResponseEvent event) {
        try {


            kafkaTemplate.send(creditResponseTopic, correlationId, event)
                    .whenComplete((result, error) -> {
                        if (error != null) {
                            log.error("Error publishing CreditResponseEvent. correlationId={}, reason={}",
                                    correlationId, error.getMessage(), error);
                            return;
                        }

                        log.info("CreditResponseEvent published. correlationId={}, found={}",
                                correlationId, event.getFound());
                    });

        }
        catch (Exception e) {
            log.error("Error serializing CreditResponseEvent. correlationId={}",
                    correlationId, e);
        }
    }
}