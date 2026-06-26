package pe.nom.charlygastelo.app.creditservice.infrastructure.client.dto;

public record AccountResponse(
        String id,
        String customerId,
        boolean active,
        String currency
) {
}
