package pe.nom.charlygastelo.app.creditservice.infrastructure.events;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.application.usecase.CheckOverdueDebtUseCase;
import pe.nom.charlygastelo.app.creditservice.infrastructure.events.mapper.CreditEventMapper;
import pe.nom.charlygastelo.app.shared.avro.dto.OverdueDebtRequestEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class OverdueDebtRequestConsumer {

    private final AvroJsonDeserializer avroJsonDeserializer;
    private final CheckOverdueDebtUseCase useCase;
    private final OverdueDebtResponseProducer producer;
    private final CreditEventMapper mapper;

    @KafkaListener(topics = "${topic.overdue-debt-request}", groupId = "credit-service")
    public void consume(String message) {

        log.info("[OverdueDebtRequestConsumer] Received raw message: {}", message);

        try {
            OverdueDebtRequestEvent event =
                    avroJsonDeserializer.deserialize(
                            message,
                            OverdueDebtRequestEvent.class,
                            OverdueDebtRequestEvent.getClassSchema()
                    );

            String correlationId = event.getCorrelationId().toString();
            String customerId = event.getCustomerId().toString();

            log.info("[OverdueDebtRequestConsumer] Parsed OverdueDebtRequestEvent. correlationId={}, customerId={}",
                    correlationId, customerId);

            useCase.execute(customerId)
                    .subscribe(
                            hasDebt -> {
                                log.info("[OverdueDebtRequestConsumer] Overdue debt check completed. customerId={}, hasDebt={}, correlationId={}",
                                        customerId, hasDebt, correlationId);

                                producer.publish(
                                        correlationId,
                                        mapper.toOverdueDebtResponseEvent(
                                                correlationId,
                                                customerId,
                                                hasDebt
                                        )
                                );

                                log.info("[OverdueDebtRequestConsumer] OverdueDebtResponseEvent published. correlationId={}",
                                        correlationId);
                            },
                            error -> {
                                log.error("[OverdueDebtRequestConsumer] Error checking overdue debt. customerId={}, correlationId={}, error={}",
                                        customerId, correlationId, error.getMessage(), error);
                            }
                    );

        } catch (Exception e) {
            log.error("[OverdueDebtRequestConsumer] Error processing OverdueDebtRequestEvent. rawMessage={}, error={}",
                    message, e.getMessage(), e);
        }
    }
}
