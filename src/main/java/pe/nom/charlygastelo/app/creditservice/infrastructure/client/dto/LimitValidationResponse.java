package pe.nom.charlygastelo.app.creditservice.infrastructure.client.dto;

public record LimitValidationResponse(
        boolean exceeded,
        String reason
) {
}
