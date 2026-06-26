package pe.nom.charlygastelo.app.creditservice.infrastructure.client.dto;

import java.math.BigDecimal;

public record LimitValidationRequest(
        String customerId,
        BigDecimal amount
) {
}

