package pe.nom.charlygastelo.app.creditservice.infrastructure.client.dto;

/**
 * Respuesta recibida desde customer-service.
 */
public record CustomerResponse(
        String id,
        String name,
        String type,
        String status
) {
}
