package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.event.mapper;

import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;
import pe.nom.charlygastelo.app.shared.avro.dto.CreditChargedEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.CreditCreatedEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.CreditDeletedEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.CreditOverdueEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.CreditPaidEvent;
import pe.nom.charlygastelo.app.shared.avro.dto.CreditResponseEvent;
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
                .setCreditId(value(credit.id()))
                .setCustomerId(value(credit.customerId()))
                .setCreditType(credit.type().name())
                .setCreditLimit(credit.creditLimit().doubleValue())
                .setBalance(credit.balance().doubleValue())
                .setStatus(credit.status().name())
                .build();
    }

    public CreditUpdatedEvent toCreditUpdatedEvent(Credit credit) {
        return CreditUpdatedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("CREDIT_UPDATED")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("credit-service")
                .setCreditId(value(credit.id()))
                .setCustomerId(value(credit.customerId()))
                .setCreditType(credit.type().name())
                .setCreditLimit(credit.creditLimit().doubleValue())
                .setBalance(credit.balance().doubleValue())
                .setStatus(credit.status().name())
                .build();
    }

    public CreditPaidEvent toCreditPaidEvent(Credit credit, double amount) {
        return CreditPaidEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("CREDIT_PAID")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("credit-service")
                .setCreditId(value(credit.id()))
                .setCustomerId(value(credit.customerId()))
                .setAmount(amount)
                .setRemainingBalance(credit.balance().doubleValue())
                .build();
    }

    public CreditChargedEvent toCreditChargedEvent(Credit credit, double amount) {
        return CreditChargedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("CREDIT_CHARGED")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("credit-service")
                .setCreditId(value(credit.id()))
                .setCustomerId(value(credit.customerId()))
                .setAmount(amount)
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
                .setCreditId(value(credit.id()))
                .setCustomerId(value(credit.customerId()))
                .setStatus(credit.status().name())
                .build();
    }

    public CreditDeletedEvent toCreditDeletedEvent(Credit credit) {
        return CreditDeletedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("CREDIT_DELETED")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("credit-service")
                .setCreditId(value(credit.id()))
                .build();
    }

    public CreditResponseEvent toCreditResponseEvent(
            Credit credit,
            String correlationId) {

        return CreditResponseEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("CREDIT_RESPONSE")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("credit-service")
                .setCorrelationId(value(correlationId))
                .setFound(true)
                .setCreditId(value(credit.id()))
                .setCustomerId(value(credit.customerId()))
                .setNumber(value(credit.number()))
                .setType(credit.type().name())
                .setStatus(credit.status().name())
                .setCreditLimit(credit.creditLimit().doubleValue())
                .setBalance(credit.balance().doubleValue())
                .setAvailableBalance(credit.availableBalance().doubleValue())
                .setOverdue(credit.overdue())
                .build();
    }

    public CreditResponseEvent toCreditNotFoundEvent(
            String creditId,
            String correlationId) {

        return CreditResponseEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("CREDIT_RESPONSE")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("credit-service")
                .setCorrelationId(value(correlationId))
                .setFound(false)
                .setCreditId(value(creditId))
                .setCustomerId("")
                .setNumber("")
                .setType("")
                .setStatus("")
                .setCreditLimit(0.0)
                .setBalance(0.0)
                .setAvailableBalance(0.0)
                .setOverdue(false)
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
                .setCorrelationId(value(correlationId))
                .setCustomerId(value(customerId))
                .setHasOverdueDebt(hasOverdueDebt)
                .build();
    }

    private String value(String value) {
        return value == null ? "" : value;
    }
}