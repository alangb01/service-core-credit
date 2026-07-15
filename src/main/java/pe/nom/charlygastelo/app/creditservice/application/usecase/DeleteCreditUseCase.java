package pe.nom.charlygastelo.app.creditservice.application.usecase;

import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.domain.exception.CreditNotFoundException;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditRepositoryPort;
import pe.nom.charlygastelo.app.creditservice.domain.port.event.CreditManagementEventProducerPort;

@RequiredArgsConstructor
@Component
@Slf4j
public class DeleteCreditUseCase {

    private final CreditRepositoryPort repository;
    private final CreditManagementEventProducerPort producer;

    public Completable execute(String id) {
        return repository.findById(id)
                .switchIfEmpty(Single.error(new CreditNotFoundException("Credit not found")))
                .flatMapCompletable(credit ->
                        repository.deleteById(id)
                                .andThen(producer.publishCreditDeleted(credit))
                );
    }
}