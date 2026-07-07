package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.client.dto;


public record FraudCheckResponse(
        boolean suspicious,
        String reason
) {
}
