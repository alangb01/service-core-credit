package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.event.mapper;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.domain.model.Transaction;
import pe.nom.charlygastelo.app.creditservice.domain.model.TransactionType;
import pe.nom.charlygastelo.app.shared.avro.dto.TransactionCreatedEvent;


@Component
@Slf4j
public class TransactionEventConsumerMapper {
    public Transaction toDomain(TransactionCreatedEvent event) {
        return new Transaction(
                event.getTransactionId().toString(),
                event.getCustomerId().toString(),
                event.getSourceProductId().toString(),
                event.getTargetProductId().toString(),
                safeValueOf(event.getTransactionType().toString()),
                new BigDecimal(event.getAmount()),
                new BigDecimal(event.getCommission()),
                event.getDescription().toString()
        );
    }

    private String value(CharSequence value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return value.toString();
    }

    public TransactionType safeValueOf(String raw) {
        try {
            return TransactionType.valueOf(raw);
        } catch (Exception ex) {
            log.warn("Unknown transaction type in context: {}", raw);
            return TransactionType.OTHER;
        }
    }
}
