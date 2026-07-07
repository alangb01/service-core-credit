package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.client.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Respuesta de movimiento obtenida desde transaction-service.
 */
public record TransactionResponse(
        String id,
        String productId,
        String customerId,
        String productType,
        String transactionType,
        BigDecimal amount,
        String description,
        LocalDateTime createdAt
) {
}
