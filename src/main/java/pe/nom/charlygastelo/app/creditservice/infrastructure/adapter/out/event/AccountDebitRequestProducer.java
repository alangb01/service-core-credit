package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.event;


import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.shared.avro.dto.AccountDebitRequestEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountDebitRequestProducer {

    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;

    @Value("${topic.account-debit-request}")
    private String accountDebitRequestTopic;

    public void send(
            String correlationId,
            AccountDebitRequestEvent event) {

        try {


            log.info(
                    "Sending AccountDebitRequestEvent. topic={}, correlationId={}, transactionId={}, accountId={}",
                    accountDebitRequestTopic,
                    correlationId,
                    event.getTransactionId(),
                    event.getAccountId()
            );

            kafkaTemplate.send(accountDebitRequestTopic, correlationId, event)
                    .whenComplete((result, error) -> {
                        if (error != null) {
                            log.error(
                                    "Error sending AccountDebitRequestEvent. topic={}, correlationId={}, reason={}",
                                    accountDebitRequestTopic,
                                    correlationId,
                                    error.getMessage(),
                                    error
                            );
                            return;
                        }

                        log.info(
                                "AccountDebitRequestEvent sent successfully. topic={}, correlationId={}, partition={}, offset={}",
                                accountDebitRequestTopic,
                                correlationId,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        );
                    });

        }
        catch (Exception e) {
            log.error(
                    "Error serializing AccountDebitRequestEvent. correlationId={}, reason={}",
                    correlationId,
                    e.getMessage(),
                    e
            );
        }
    }
}