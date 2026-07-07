package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.client.dto;

public record LimitValidationResponse(
        boolean exceeded,
        String reason
) {
}
