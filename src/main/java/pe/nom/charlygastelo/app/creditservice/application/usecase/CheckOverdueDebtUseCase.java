package pe.nom.charlygastelo.app.creditservice.application.usecase;

import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditRepositoryPort;

@RequiredArgsConstructor
@Slf4j
public class CheckOverdueDebtUseCase {

    private final CreditRepositoryPort repository;

    public Single<Boolean> execute(String customerId) {
        return repository.existsOverdueDebtByCustomerId(customerId);
    }
}