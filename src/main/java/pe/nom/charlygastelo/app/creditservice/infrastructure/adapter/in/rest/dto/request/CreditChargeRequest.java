package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.rest.dto.request;

import java.math.BigDecimal;

public record CreditChargeRequest(
        BigDecimal amount
) { }