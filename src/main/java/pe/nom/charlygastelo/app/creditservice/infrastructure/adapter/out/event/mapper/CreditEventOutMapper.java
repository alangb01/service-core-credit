package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.event.mapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.application.command.CreditPaymentCommand;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;
import pe.nom.charlygastelo.app.creditservice.domain.model.Transaction;
import pe.nom.charlygastelo.app.shared.avro.dto.*;


@Component
@Slf4j
public class CreditEventOutMapper {

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
                .setAvailableBalance(credit.available().doubleValue())
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
                .setAvailableBalance(credit.available().doubleValue())
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

    public CreditInterestOccurredEvent toCreditInterestChargeEvent(String creditId, String interestAmount, String cycle) {
        return CreditInterestOccurredEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("CREDIT_INTEREST_CALCULATED")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("credit-service")

                .setCreditId(value(creditId))

                .build();
    }



    public CreditPaymentCommand toCommand(AccountWithdrawOccurredEvent event) {
        return new CreditPaymentCommand(
                event.getTransactionId().toString(),
                event.getCreditId().toString(),
                event.getCustomerId().toString(),
                BigDecimal.valueOf(event.getAmount()),
                event.getAccountId().toString(),
                event.getReason().toString()
        );
    }

    public CreditPaymentOccurredEvent toCreditPaymentOccurred(Credit credit, Transaction tx) {

        return CreditPaymentOccurredEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("CREDIT_PAYMENT_OCCURRED")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("credit-service")

                .setTransactionId(tx.id())
                .setCreditId(credit.id())
                .setAccountId(tx.sourceProductId())
                .setCustomerId(tx.customerId())

                .setAmount(tx.amount().doubleValue())
                .setBalance(credit.balance().doubleValue())
                .setAvailable(credit.available().doubleValue())

                .build();
    }

    public CreditWithdrawOccurredEvent toCreditWithdrawOccurred(Credit credit, Transaction tx) {

        return CreditWithdrawOccurredEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("CREDIT_WITHDRAW_OCCURRED")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("credit-service")

                .setTransactionId(tx.id())
                .setCreditId(credit.id())
                .setAccountId(tx.targetProductId())
                .setCustomerId(tx.customerId())

                .setAmount(tx.amount().doubleValue())
                .setBalance(credit.balance().doubleValue())
                .setAvailable(credit.available().doubleValue())

                .build();
    }


    public CreditInterestOccurredEvent toInterestCalculatedOccurred(Credit credit, Transaction tx) {
        return CreditInterestOccurredEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("CREDIT_INTEREST_CALCULATED")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("credit-service")


                .build();
    }
}