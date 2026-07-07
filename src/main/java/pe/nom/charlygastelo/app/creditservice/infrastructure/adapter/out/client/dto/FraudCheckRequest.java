package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.client.dto;

import java.math.BigDecimal;

public record FraudCheckRequest(
        String creditId,
        String customerId,
        BigDecimal amount
) {
}