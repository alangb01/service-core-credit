package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.event;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditRepositoryPort;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.event.mapper.CreditEventMapper;
import pe.nom.charlygastelo.app.shared.avro.dto.CreditRequestEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreditRequestConsumer {

    private final AvroJsonDeserializer deserializer;
    private final CreditRepositoryPort repository;
    private final CreditResponseProducer responseProducer;
    private final CreditEventMapper mapper;

    @KafkaListener(topics = "${topic.credit-request}", groupId = "credit-service")
    public void consume(String message) {
        try {
            CreditRequestEvent event = deserializer.deserialize(
                    message,
                    CreditRequestEvent.class,
                    CreditRequestEvent.getClassSchema()
            );

            String correlationId = event.getCorrelationId().toString();
            String creditId = event.getCreditId().toString();

            log.info("CreditRequestEvent received. correlationId={}, creditId={}",
                    correlationId, creditId);

            repository.findById(creditId)
                    .subscribe(
                            credit -> responseProducer.publish(
                                    correlationId,
                                    mapper.toCreditResponseEvent(credit, correlationId)
                            ),
                            error -> {
                                log.error("Error searching credit. correlationId={}, creditId={}, reason={}",
                                        correlationId, creditId, error.getMessage(), error);

                                responseProducer.publish(
                                        correlationId,
                                        mapper.toCreditNotFoundEvent(creditId, correlationId)
                                );
                            },
                            () -> {
                                log.warn("Credit not found. correlationId={}, creditId={}",
                                        correlationId, creditId);

                                responseProducer.publish(
                                        correlationId,
                                        mapper.toCreditNotFoundEvent(creditId, correlationId)
                                );
                            }
                    );

        }
        catch (Exception e) {
            log.error("Error processing CreditRequestEvent", e);
        }
    }
}