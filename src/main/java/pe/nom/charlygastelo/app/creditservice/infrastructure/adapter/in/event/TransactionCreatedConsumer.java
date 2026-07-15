package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.application.usecase.transaction.CreatedTransactionUseCase;
import pe.nom.charlygastelo.app.creditservice.domain.model.Transaction;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.event.mapper.TransactionEventConsumerMapper;
import pe.nom.charlygastelo.app.shared.avro.dto.TransactionCreatedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionCreatedConsumer {

    private final CreatedTransactionUseCase useCase;
    private final TransactionEventConsumerMapper mapper;

    @KafkaListener(topics = "${topic.transaction-created}", groupId = "credit-service")
    public void consume(TransactionCreatedEvent event) {

        try {
            Transaction tx = mapper.toDomain(event);

            log.info("[CREDIT] Received TRANSACTION_CREATED txId={}, type={}", tx.id(), tx.type());
            log.debug("[CREDIT] Deserializing event. event={}", event);

            if (!useCase.isCreditServiceResponsible(tx.type())) {
                log.info("[CREDIT] Ignoring txId={} type={} (not credit responsibility)", tx.id(), tx.type());
                return;
            }

            useCase.execute(tx).subscribe();

        } catch (Exception ex) {
            log.error("[CREDIT] Fatal error: {}", ex.getMessage());
        }
    }
}
