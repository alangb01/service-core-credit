package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditRepositoryPort;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.event.ActiveCreditCardResponseProducer;
import pe.nom.charlygastelo.app.shared.avro.dto.ActiveCreditCardRequestEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActiveCreditCardRequestConsumer {

    private final CreditRepositoryPort repository;
    private final ActiveCreditCardResponseProducer producer;

    @KafkaListener(
            topics = "${topic.active-credit-card-request}",
            groupId = "credit-service")
    public void consume(ActiveCreditCardRequestEvent event) {

        try {

            String correlationId = event.getCorrelationId().toString();
            String customerId = event.getCustomerId().toString();

            log.info(
                    "ActiveCreditCardRequestEvent received. correlationId={}, customerId={}",
                    correlationId,
                    customerId
            );

            repository.hasActiveCreditCard(customerId)
                    .subscribe(hasCard ->
                                    producer.publish(
                                            correlationId,
                                            customerId,
                                            hasCard
                                    ),
                            error ->
                                    log.error(
                                            "Error validating active credit card. customerId={}, reason={}",
                                            customerId,
                                            error.getMessage(),
                                            error
                                    ));

        } catch (Exception e) {
            log.error(
                    "Error consuming ActiveCreditCardRequestEvent",
                    e
            );
        }
    }
}