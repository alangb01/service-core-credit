package pe.nom.charlygastelo.app.creditservice.application.command;

import java.math.BigDecimal;

public record CreditPaymentCommand(
        String transactionId,
        String creditId,
        String customerId,
        BigDecimal amount,
        String accountId,
        String reason
) { }
