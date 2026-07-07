package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.client.dto;

import java.math.BigDecimal;

public record LimitValidationRequest(
        String customerId,
        BigDecimal amount
) {
}

