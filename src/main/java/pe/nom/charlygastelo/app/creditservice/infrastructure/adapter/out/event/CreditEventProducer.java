package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.event;

import java.math.BigDecimal;


import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import io.reactivex.rxjava3.core.Completable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditEventProducerPort;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.event.mapper.CreditEventMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreditEventProducer implements CreditEventProducerPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AvroJsonSerializer avroJsonSerializer;
    private final CreditEventMapper mapper;

    @Value("${topic.credit-created}")
    private String creditCreatedTopic;

    @Value("${topic.credit-updated}")
    private String creditUpdatedTopic;

    @Value("${topic.credit-paid}")
    private String creditPaidTopic;

    @Value("${topic.credit-charged}")
    private String creditChargedTopic;

    @Value("${topic.credit-overdue}")
    private String creditOverdueTopic;

    @Value("${topic.credit-deleted}")
    private String creditDeletedTopic;

    @Override
    public Completable publishCreditCreated(Credit credit) {
        log.info("Publishing CreditCreatedEvent. creditId={}, customerId={}",
                credit.id(), credit.customerId());

        return publish(
                creditCreatedTopic,
                credit.id(),
                mapper.toCreditCreatedEvent(credit)
        );
    }

    @Override
    public Completable publishCreditUpdated(Credit credit) {
        log.info("Publishing CreditUpdatedEvent. creditId={}, customerId={}",
                credit.id(), credit.customerId());

        return publish(
                creditUpdatedTopic,
                credit.id(),
                mapper.toCreditUpdatedEvent(credit)
        );
    }

    @Override
    public Completable publishCreditPaid(Credit credit, BigDecimal amount) {
        log.info("Publishing CreditPaidEvent. creditId={}, customerId={}, amount={}",
                credit.id(), credit.customerId(), amount);

        return publish(
                creditPaidTopic,
                credit.id(),
                mapper.toCreditPaidEvent(credit, amount.doubleValue())
        );
    }

    @Override
    public Completable publishCreditCharged(Credit credit, BigDecimal amount) {
        log.info("Publishing CreditChargedEvent. creditId={}, customerId={}, amount={}",
                credit.id(), credit.customerId(), amount);

        return publish(
                creditChargedTopic,
                credit.id(),
                mapper.toCreditChargedEvent(credit, amount.doubleValue())
        );
    }

    @Override
    public Completable publishCreditOverdue(Credit credit) {
        log.warn("Publishing CreditOverdueEvent. creditId={}, customerId={}",
                credit.id(), credit.customerId());

        return publish(
                creditOverdueTopic,
                credit.id(),
                mapper.toCreditOverdueEvent(credit)
        );
    }

    @Override
    public Completable publishCreditDeleted(Credit credit) {
        log.info("Publishing CreditDeletedEvent. creditId={}", credit.id());

        return publish(
                creditDeletedTopic,
                credit.id(),
                mapper.toCreditDeletedEvent(credit)
        );
    }

    private Completable publish(
            String topic,
            String key,
            SpecificRecordBase event) {

        return Completable.create(emitter -> {
            try {
                String payload = avroJsonSerializer.serialize(event);

                kafkaTemplate.send(topic, key, payload)
                        .whenComplete((result, error) -> {
                            if (error != null) {
                                log.error(
                                        "Error publishing credit event. topic={}, key={}, eventClass={}, reason={}",
                                        topic,
                                        key,
                                        event.getClass().getSimpleName(),
                                        error.getMessage(),
                                        error
                                );
                                emitter.onError(error);
                                return;
                            }

                            log.info(
                                    "Credit event published successfully. topic={}, key={}, eventClass={}, partition={}, offset={}",
                                    topic,
                                    key,
                                    event.getClass().getSimpleName(),
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset()
                            );

                            emitter.onComplete();
                        });

            }
            catch (Exception e) {
                log.error(
                        "Error serializing credit event. topic={}, key={}, eventClass={}, reason={}",
                        topic,
                        key,
                        event.getClass().getSimpleName(),
                        e.getMessage(),
                        e
                );
                emitter.onError(e);
            }
        });
    }
}