package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.event;

import org.apache.avro.specific.SpecificRecordBase;
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

    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;

    @Value("${topic.overdue-debt-response}")
    private String overdueDebtResponseTopic;

    public void publish(
            String correlationId,
            OverdueDebtResponseEvent event) {

        try {


            kafkaTemplate.send(overdueDebtResponseTopic, correlationId, event)
                    .whenComplete((result, error) -> {
                        if (error != null) {
                            log.error("Error publishing OverdueDebtResponseEvent. correlationId={}, reason={}",
                                    correlationId, error.getMessage(), error);
                            return;
                        }

                        log.info("OverdueDebtResponseEvent published. correlationId={}, customerId={}, hasDebt={}",
                                correlationId,
                                event.getCustomerId(),
                                event.getHasOverdueDebt());
                    });

        }
        catch (Exception e) {
            log.error("Error serializing OverdueDebtResponseEvent. correlationId={}",
                    correlationId, e);
        }
    }
}