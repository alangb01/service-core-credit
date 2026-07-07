package pe.nom.charlygastelo.app.creditservice.infrastructure.config.security;

import java.util.List;

public record UserPrincipal(
        String userId,
        String customerId,
        List<String> roles
) {
}