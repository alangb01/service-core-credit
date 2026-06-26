package pe.nom.charlygastelo.app.creditservice.infrastructure.client.dto;

import java.math.BigDecimal;

/**
 * Solicitud enviada a transaction-service para registrar un pago de crédito.
 */
public record CreateCreditPaymentRequest(
        String creditId,
        String customerId,
        BigDecimal amount
) {
}
