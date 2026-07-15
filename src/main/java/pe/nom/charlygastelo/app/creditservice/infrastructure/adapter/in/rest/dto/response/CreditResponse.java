package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.rest.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record CreditResponse(
        String id,
        String customerId,
        String number,
        String type,
        String status,
        BigDecimal creditLimit,
        BigDecimal balance,
        BigDecimal availableBalance,
        BigDecimal interestRate,
        Integer billingCycleDay,     // día de corte
        Instant nextBillingDate,     // fecha de corte
        Instant nextPaymentDate,     // fecha
        Integer installments,
        Instant dueDate,
        boolean overdue,
        Instant createdAt,
        Instant updatedAt
) { }