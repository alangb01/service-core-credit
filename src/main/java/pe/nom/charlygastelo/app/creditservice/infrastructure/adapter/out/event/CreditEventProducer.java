package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.event;

import java.math.BigDecimal;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;
import pe.nom.charlygastelo.app.creditservice.domain.model.Transaction;
import pe.nom.charlygastelo.app.creditservice.domain.port.event.CreditLedgerEventProducerPort;
import pe.nom.charlygastelo.app.creditservice.domain.port.event.CreditManagementEventProducerPort;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.event.mapper.CreditEventOutMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreditEventProducer implements CreditManagementEventProducerPort, CreditLedgerEventProducerPort {

    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;
    private final CreditEventOutMapper mapper;

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



    // LEDGER
    @Value("${topic.credit-withdraw-occurred}")
    private String creditWithdrawOccurredTopic;

    @Value("${topic.credit-payment-occurred}")
    private String creditPaymentOccuredTopic;


    @Value("${topic.credit-interest-charge-occurred}")
    private String creditInterestChargeOccurredTopic;




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

    @Override
    public Completable publishInterestCalculated(Credit credit, BigDecimal interest, String cycle) {
        log.info("Publishing Credit Interest Event. creditId={}", credit.id());

        return publish(
                creditInterestChargeOccurredTopic,
                credit.id(),
                mapper.toCreditInterestChargeEvent(credit.id(), String.valueOf(interest), cycle)
        );
    }

    @Override
    public Completable publishCreditPaymentOccurred(Credit credit, Transaction tx) {

        log.info("Publishing Credit payment occurred Event. creditId={}", credit.id());
        log.debug("[CREDIT] Serializing event. credit={}, transaction={}", credit, tx);
        return publish(
                creditPaymentOccuredTopic,
                credit.id(),
                mapper.toCreditPaymentOccurred(credit, tx)
        );
    }

    @Override
    public Completable publishCreditWithdrawOccurred(Credit credit, Transaction tx) {

        log.info("Publishing Credit withdraw occurred Event. creditId={}", credit.id());
        log.debug("[CREDIT] Serializing event. credit={}, transaction={}", credit, tx);
        return publish(
                creditWithdrawOccurredTopic,
                credit.id(),
                mapper.toCreditWithdrawOccurred(credit, tx)
        );
    }

    @Override
    public CompletableSource publishInterestChargeOccurred(Credit credit, Transaction tx) {
        log.info("Publishing Credit interest charge occurred Event. creditId={}", credit.id());
        return publish(
                creditInterestChargeOccurredTopic,
                credit.id(),
                mapper.toCreditWithdrawOccurred(credit, tx)
        );
    }


    private Completable publish(String topic, String key, SpecificRecordBase event) {
        log.info("[CREDIT-EVENT] Sending event. topic={}, key={}", topic, key);
        return Completable.fromFuture(
                kafkaTemplate.send(topic, key, event)
                        .whenComplete((result, error) -> {
                            if (error != null) {
                                log.error("[CREDIT-EVENT] Error sending event. topic={}, key={}, reason={}",
                                        topic, key, error.getMessage(), error);
                            }
                            else {
                                log.info("[CREDIT-EVENT] Event sent successfully. topic={}, key={}, " +
                                                "partition={}, offset={}",
                                        topic, key,
                                        result.getRecordMetadata().partition(),
                                        result.getRecordMetadata().offset());
                            }
                        })
        );
    }
}