package pe.nom.charlygastelo.app.creditservice.application.usecase;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.creditservice.domain.exception.CreditNotFoundException;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditEventProducerPort;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditRepositoryPort;

@RequiredArgsConstructor
public class DeleteCreditUseCase {

    private final CreditRepositoryPort repository;
    private final CreditEventProducerPort producer;

    public Completable execute(String id) {
        return repository.findById(id)
                .switchIfEmpty(Single.error(new CreditNotFoundException("Credit not found")))
                .flatMapCompletable(credit ->
                        repository.deleteById(id)
                                .andThen(producer.publishCreditDeleted(credit))
                );
    }
}