package pe.nom.charlygastelo.app.creditservice.application.usecase;

import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.domain.exception.CreditNotFoundException;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditRepositoryPort;
import pe.nom.charlygastelo.app.creditservice.domain.port.event.CreditManagementEventProducerPort;

@RequiredArgsConstructor
@Component
@Slf4j
public class UpdateCreditUseCase {

    private final CreditRepositoryPort repository;
    private final CreditManagementEventProducerPort producer;

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