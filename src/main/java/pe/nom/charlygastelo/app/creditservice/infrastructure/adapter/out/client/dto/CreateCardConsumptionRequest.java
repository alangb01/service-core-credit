package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.client.dto;

import java.math.BigDecimal;

/**
 * Solicitud enviada a transaction-service para registrar consumo de tarjeta.
 */
public record CreateCardConsumptionRequest(
        String creditId,
        String customerId,
        BigDecimal amount,
        String description
) {
}
