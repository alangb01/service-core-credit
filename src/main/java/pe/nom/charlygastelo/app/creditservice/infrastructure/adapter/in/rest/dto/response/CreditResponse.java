package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.rest.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
        Integer installments,
        LocalDate dueDate,
        boolean overdue,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) { }