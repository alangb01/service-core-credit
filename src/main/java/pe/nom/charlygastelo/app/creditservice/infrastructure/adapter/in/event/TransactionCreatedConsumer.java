package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.application.usecase.ProcessCreditTransactionUseCase;
import pe.nom.charlygastelo.app.shared.avro.dto.TransactionCreatedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionCreatedConsumer {

    private final ProcessCreditTransactionUseCase useCase;

    @KafkaListener(
            topics = "${topic.transaction-created}",
            groupId = "credit-service")
    public void consume(TransactionCreatedEvent event) {
        log.debug("TransactionCreatedEvent raw message received by credit-service");

        try {

            String transactionType = event.getTransactionType().toString();

            boolean supported =
                    "CREDIT_PAYMENT".equalsIgnoreCase(transactionType)
                            || "CREDIT_CARD_CHARGE".equalsIgnoreCase(transactionType)
                        || "CREDIT_WITHDRAWL".equalsIgnoreCase(transactionType);

            if (!supported) {
                log.info(
                        "Transaction ignored by credit-service. transactionId={}, type={}",
                        event.getTransactionId(),
                        transactionType
                );
                return;
            }

            log.info(
                    "TransactionCreatedEvent accepted by credit-service. transactionId={}, type={}",
                    event.getTransactionId(),
                    transactionType
            );

            useCase.execute(event)
                    .subscribe(
                            () -> log.info(
                                    "Credit transaction processed successfully. transactionId={}",
                                    event.getTransactionId()
                            ),
                            error -> log.error(
                                    "Error processing credit transaction. transactionId={}, reason={}",
                                    event.getTransactionId(),
                                    error.getMessage(),
                                    error
                            )
                    );

        }
        catch (Exception e) {
            log.error(
                    "Error consuming TransactionCreatedEvent in credit-service. reason={}",
                    e.getMessage(),
                    e
            );
        }
    }
}