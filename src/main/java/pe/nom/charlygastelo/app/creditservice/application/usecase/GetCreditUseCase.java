package pe.nom.charlygastelo.app.creditservice.application.usecase;

import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Maybe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.domain.exception.CreditNotFoundException;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditCachePort;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditRepositoryPort;

/**
 * Use case responsible for retrieving a credit by its identifier.
 * Uses Redis cache first, then MongoDB, with safe fallback.
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class GetCreditUseCase {

    private final CreditRepositoryPort repository;
    private final CreditCachePort cache;

    /**
     * Retrieves a credit by ID using cache first.
     *
     * @param id credit identifier
     * @return Maybe<Credit>
     */
    public Maybe<Credit> byId(String id) {

        log.info("[GetCreditUseCase] INIT find credit. creditId={}", id);

        return cache.getById(id)
                .doOnSuccess(c ->
                        log.info("[GetCreditUseCase] CACHE HIT. creditId={}, customerId={}",
                                c.id(), c.customerId())
                )
                .onErrorResumeNext(error -> {
                    log.warn("[GetCreditUseCase] CACHE ERROR (fallback to DB). creditId={}, reason={}",
                            id, error.getMessage());
                    return Maybe.empty();
                })
                .switchIfEmpty(
                        Maybe.defer(() ->
                                repository.findById(id)
                                        .doOnSuccess(c ->
                                                log.info("[GetCreditUseCase] DB HIT. creditId={}, customerId={}",
                                                        c.id(), c.customerId())
                                        )
                                        .flatMap(credit ->
                                                cache.save(credit)
                                                        .onErrorComplete(e -> {
                                                            log.warn("[GetCreditUseCase] CACHE PUT ERROR (ignored). creditId={}, reason={}",
                                                                    id, e.getMessage());
                                                            return true;
                                                        })
                                                        .andThen(Maybe.just(credit))
                                        )
                        )
                )
                .switchIfEmpty(
                        Maybe.error(new CreditNotFoundException("Credit not found: " + id))
                )
                .doOnError(error ->
                        log.error("[GetCreditUseCase] ERROR retrieving credit. creditId={}, errorType={}, reason={}",
                                id, error.getClass().getSimpleName(), error.getMessage(), error)
                );
    }
}
