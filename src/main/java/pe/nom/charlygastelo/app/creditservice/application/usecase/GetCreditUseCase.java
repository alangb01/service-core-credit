package pe.nom.charlygastelo.app.creditservice.application.usecase;

import io.reactivex.rxjava3.core.Maybe;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.creditservice.domain.exception.CreditNotFoundException;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditRepositoryPort;

@RequiredArgsConstructor
public class GetCreditUseCase {

    private final CreditRepositoryPort repository;

    public Maybe<Credit> byId(String id) {
        return repository.findById(id)
                .switchIfEmpty(Maybe.error(new CreditNotFoundException("Credit not found: " + id)));
    }
}