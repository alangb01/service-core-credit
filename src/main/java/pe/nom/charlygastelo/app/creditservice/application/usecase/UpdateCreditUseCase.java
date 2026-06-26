package pe.nom.charlygastelo.app.creditservice.application.usecase;

import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.creditservice.domain.exception.CreditNotFoundException;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditEventProducerPort;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditRepositoryPort;

@RequiredArgsConstructor
public class UpdateCreditUseCase {

    private final CreditRepositoryPort repository;
    private final CreditEventProducerPort producer;

    public Single<Credit> execute(String id, Credit credit) {
        return repository.findById(id)
                .switchIfEmpty(Single.error(new CreditNotFoundException("Credit not found")))
                .flatMap(existing -> repository.save(credit.withId(id)))
                .flatMap(saved ->
                        producer.publishCreditUpdated(saved)
                                .andThen(Single.just(saved))
                );
    }
}