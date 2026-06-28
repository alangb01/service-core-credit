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

    private final AvroJsonDeserializer deserializer;
    private final CheckOverdueDebtUseCase useCase;
    private final OverdueDebtResponseProducer producer;
    private final CreditEventMapper mapper;

    @KafkaListener(topics = "${topic.overdue-debt-request}", groupId = "credit-service")
    public void consume(String message) {
        try {
            OverdueDebtRequestEvent event =
                    deserializer.deserialize(
                            message,
                            OverdueDebtRequestEvent.class,
                            OverdueDebtRequestEvent.getClassSchema()
                    );

            String correlationId = event.getCorrelationId().toString();
            String customerId = event.getCustomerId().toString();

            log.info("OverdueDebtRequestEvent received. correlationId={}, customerId={}",
                    correlationId, customerId);

            useCase.execute(customerId)
                    .subscribe(
                            hasDebt -> {
                                log.info("Overdue debt checked. customerId={}, hasDebt={}",
                                        customerId, hasDebt);

                                producer.publish(
                                        correlationId,
                                        mapper.toOverdueDebtResponseEvent(
                                                correlationId,
                                                customerId,
                                                hasDebt
                                        )
                                );
                            },
                            error -> log.error(
                                    "Error checking overdue debt. correlationId={}, customerId={}, reason={}",
                                    correlationId,
                                    customerId,
                                    error.getMessage(),
                                    error
                            )
                    );

        }
        catch (Exception e) {
            log.error("Error processing OverdueDebtRequestEvent", e);
        }
    }
}