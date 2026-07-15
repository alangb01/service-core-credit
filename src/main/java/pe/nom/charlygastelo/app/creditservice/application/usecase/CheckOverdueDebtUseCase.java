package pe.nom.charlygastelo.app.creditservice.application.usecase;

import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditRepositoryPort;

@RequiredArgsConstructor
@Slf4j
@Component
public class CheckOverdueDebtUseCase {

    private final CreditRepositoryPort repository;

    /**
     * Verifica si el cliente tiene deuda vencida.
     * Devuelve Single<Boolean>:
     *  - true  → tiene deuda vencida
     *  - false → no tiene deuda vencida
     */
    public Single<Boolean> execute(String customerId) {

        log.info("[CheckOverdueDebtUseCase] Checking overdue debt. customerId={}", customerId);

        return repository.existsOverdueDebtByCustomerId(customerId)
                .doOnSubscribe(d ->
                        log.debug("[CheckOverdueDebtUseCase] Querying repository for overdue debt. customerId={}", customerId)
                )
                .doOnSuccess(hasDebt ->
                        log.info("[CheckOverdueDebtUseCase] Overdue debt check completed. customerId={}, hasDebt={}",
                                customerId, hasDebt)
                )
                .doOnError(error ->
                        log.error("[CheckOverdueDebtUseCase] Error checking overdue debt. customerId={}, errorType={}, reason={}",
                                customerId,
                                error.getClass().getSimpleName(),
                                error.getMessage(),
                                error)
                );
    }
}
