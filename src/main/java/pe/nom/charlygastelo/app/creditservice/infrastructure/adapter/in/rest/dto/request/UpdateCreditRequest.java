package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.rest.dto.request;

import java.math.BigDecimal;
import java.time.Instant;

public record UpdateCreditRequest(
        String number,
        String type,
        String status,
        BigDecimal creditLimit,
        BigDecimal balance,
        BigDecimal availableBalance,
        BigDecimal interestRate,
        Integer installments,
        Instant dueDate,
        boolean overdue
) { }