package pe.nom.charlygastelo.app.creditservice.application.usecase;

import java.util.List;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditRepositoryPort;

@Slf4j
@RequiredArgsConstructor
public class ListCreditsUseCase {

    private final CreditRepositoryPort repository;

    public Flowable<Credit> all() {
        log.info("Listing all credits");

        return repository.findAll()
                .doOnSubscribe(subscription ->
                        log.debug("Starting MongoDB findAll()")
                )
                .doOnNext(credit ->
                        log.debug("Credit loaded. id={}, number={}",
                                credit.id(), credit.number())
                )
                .doOnComplete(() ->
                        log.info("All credits loaded successfully")
                )
                .doOnError(error ->
                        log.error("Error listing credits: {}",
                                error.getMessage(), error)
                );
    }

    public Single<List<Credit>> findByCustomerId(String customerId) {
        log.info("Listing credits for customer {}", customerId);

        return repository.findByCustomerId(customerId)
                .doOnSubscribe(subscription ->
                        log.debug("Starting findByCustomerId({})", customerId)
                )
                .doOnNext(credit ->
                        log.debug("Credit by customer loaded. id={}, number={}",
                                credit.id(), credit.number())
                )
                .doOnComplete(() ->
                        log.info("Credits by customer loaded successfully {}", customerId)
                ).toList()
                .doOnError(error ->
                        log.error("Error listing credits for customer {}: {}",
                                customerId, error.getMessage(), error)
                );
    }
}