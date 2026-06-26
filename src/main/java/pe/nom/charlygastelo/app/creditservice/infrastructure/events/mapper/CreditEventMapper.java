package pe.nom.charlygastelo.app.creditservice.infrastructure.events.mapper;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;
import pe.nom.charlygastelo.app.shared.avro.dto.CreditChargedEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.CreditCreatedEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.CreditDeletedEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.CreditOverdueEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.CreditPaidEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.CreditUpdatedEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.OverdueDebtResponseEvent;

@Component
public class CreditEventMapper {

    public CreditCreatedEvent toCreditCreatedEvent(Credit credit) {

        return CreditCreatedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("CREDIT_CREATED")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("credit-service")
                .setCreditId(credit.id())
                .setCustomerId(credit.customerId())
                .setNumber(credit.number())
                .setType(credit.type().name())
                .setStatus(credit.status().name())
                .setCreditLimit(credit.creditLimit().doubleValue())
                .setBalance(credit.balance().doubleValue())
                .setAvailableBalance(credit.availableBalance().doubleValue())
                .build();
    }

    public CreditUpdatedEvent toCreditUpdatedEvent(Credit credit) {

        return CreditUpdatedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("CREDIT_UPDATED")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("credit-service")
                .setCreditId(credit.id())
                .setCustomerId(credit.customerId())
                .setNumber(credit.number())
                .setType(credit.type().name())
                .setStatus(credit.status().name())
                .setCreditLimit(credit.creditLimit().doubleValue())
                .setBalance(credit.balance().doubleValue())
                .setAvailableBalance(credit.availableBalance().doubleValue())
                .build();
    }

    public CreditPaidEvent toCreditPaidEvent(Credit credit) {

        return CreditPaidEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("CREDIT_PAID")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("credit-service")
                .setCreditId(credit.id())
                .setCustomerId(credit.customerId())
                .setAmount(credit.balance().doubleValue())
                .setBalance(credit.balance().doubleValue())
                .build();
    }

    public CreditChargedEvent toCreditChargedEvent(Credit credit) {

        return CreditChargedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("CREDIT_CHARGED")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("credit-service")
                .setCreditId(credit.id())
                .setCustomerId(credit.customerId())
                .setAmount(credit.balance().doubleValue())
                .setAvailableBalance(credit.availableBalance().doubleValue())
                .build();
    }

    public CreditOverdueEvent toCreditOverdueEvent(Credit credit) {

        return CreditOverdueEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("CREDIT_OVERDUE")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("credit-service")
                .setCreditId(credit.id())
                .setCustomerId(credit.customerId())
                .setDueDate(credit.dueDate().toString())
                .build();
    }

    public CreditDeletedEvent toCreditDeletedEvent(Credit credit) {

        return CreditDeletedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("CREDIT_DELETED")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("credit-service")
                .setCreditId(credit.id())
                .build();
    }

    public OverdueDebtResponseEvent toOverdueDebtResponseEvent(
            String correlationId,
            String customerId,
            boolean hasOverdueDebt) {

        return OverdueDebtResponseEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("OVERDUE_DEBT_RESPONSE")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("credit-service")
                .setCorrelationId(correlationId)
                .setCustomerId(customerId)
                .setHasOverdueDebt(hasOverdueDebt)
                .build();
    }
}