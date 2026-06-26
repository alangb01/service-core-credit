package pe.nom.charlygastelo.app.creditservice.infrastructure.events;


import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import io.reactivex.rxjava3.core.Completable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditEventProducerPort;
import pe.nom.charlygastelo.app.creditservice.infrastructure.events.mapper.CreditEventMapper;

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
        return publish(creditCreatedTopic, credit.id(), mapper.toCreditCreatedEvent(credit));
    }

    @Override
    public Completable publishCreditUpdated(Credit credit) {
        return publish(creditUpdatedTopic, credit.id(), mapper.toCreditUpdatedEvent(credit));
    }

    @Override
    public Completable publishCreditPaid(Credit credit) {
        return publish(creditPaidTopic, credit.id(), mapper.toCreditPaidEvent(credit));
    }

    @Override
    public Completable publishCreditCharged(Credit credit) {
        return publish(creditChargedTopic, credit.id(), mapper.toCreditChargedEvent(credit));
    }

    @Override
    public Completable publishCreditOverdue(Credit credit) {
        return publish(creditOverdueTopic, credit.id(), mapper.toCreditOverdueEvent(credit));
    }

    @Override
    public Completable publishCreditDeleted(Credit credit) {
        return publish(creditDeletedTopic, credit.id(), mapper.toCreditDeletedEvent(credit));
    }

    private Completable publish(String topic, String key, SpecificRecordBase event) {
        return Completable.create(emitter -> {
            try {
                String payload = avroJsonSerializer.serialize(event);

                kafkaTemplate.send(topic, key, payload)
                        .whenComplete((result, error) -> {
                            if (error != null) {
                                emitter.onError(error);
                            }
                            else {
                                log.info("Credit event published. topic={}, key={}", topic, key);
                                emitter.onComplete();
                            }
                        });

            }
            catch (Exception e) {
                emitter.onError(e);
            }
        });
    }
}