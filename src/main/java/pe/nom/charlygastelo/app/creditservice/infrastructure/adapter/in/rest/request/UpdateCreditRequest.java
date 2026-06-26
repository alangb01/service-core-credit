package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.rest.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateCreditRequest(
        String number,
        String type,
        String status,
        BigDecimal creditLimit,
        BigDecimal balance,
        BigDecimal availableBalance,
        BigDecimal interestRate,
        Integer installments,
        LocalDate dueDate,
        boolean overdue
) { }