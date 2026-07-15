package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.rest.dto.request;

import java.math.BigDecimal;

public record CreateCreditRequest(
        String customerId,
        String number,
        String type,
        BigDecimal creditLimit,
        BigDecimal balance,
        BigDecimal interestRate,
        Integer installments,
        Integer billingCycleDay
) { }