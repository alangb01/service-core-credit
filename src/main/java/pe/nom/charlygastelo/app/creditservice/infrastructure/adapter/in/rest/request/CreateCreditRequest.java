package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.rest.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateCreditRequest(
        String customerId,
        String number,
        String type,
        BigDecimal creditLimit,
        BigDecimal balance,
        BigDecimal interestRate,
        Integer installments,
        LocalDate dueDate
) { }