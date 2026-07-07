package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.client.dto;

public record AccountResponse(
        String id,
        String customerId,
        boolean active,
        String currency
) {
}
