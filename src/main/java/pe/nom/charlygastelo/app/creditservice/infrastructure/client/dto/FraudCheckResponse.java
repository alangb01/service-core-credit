package pe.nom.charlygastelo.app.creditservice.infrastructure.client.dto;


public record FraudCheckResponse(
        boolean suspicious,
        String reason
) {
}
