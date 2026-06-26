package pe.nom.charlygastelo.app.creditservice.infrastructure.client.dto;

import java.math.BigDecimal;

public record CreateDisbursementRequest(
        String creditId,
        String customerId,
        String accountId,
        BigDecimal amount
) {
}
