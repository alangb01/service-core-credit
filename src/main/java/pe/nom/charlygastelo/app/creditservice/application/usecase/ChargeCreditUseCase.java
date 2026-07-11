package pe.nom.charlygastelo.app.creditservice.application.usecase;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.nom.charlygastelo.app.creditservice.domain.exception.BusinessRuleException;
import pe.nom.charlygastelo.app.creditservice.domain.exception.CreditNotFoundException;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;
import pe.nom.charlygastelo.app.creditservice.domain.model.CreditType;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditCachePort;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditEventProducerPort;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditRepositoryPort;

import java.math.BigDecimal;

/**
 * Use case responsible for processing credit card charges.
 * Applies business rules, updates balances, publishes domain events,
 * and integrates reactive Redis cache with safe fallback.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChargeCreditUseCase {

    private final CreditRepositoryPort repository;
    private final CreditEventProducerPort producer;
    private final CreditCachePort cache; // Reactive Redis cache port

    /**
     * Executes a credit card charge operation.
     *
     * @param creditId credit identifier
     * @param amount   charge amount
     * @return updated credit entity
     */
    public Single<Credit> execute(String creditId, BigDecimal amount) {

        log.info("[ChargeCreditCardUseCase] INIT charge. creditId={}, amount={}", creditId, amount);

        return validateAmount(amount)
                .andThen(loadCreditWithCache(creditId))
                .flatMap(credit -> validateCreditCard(credit, amount))
                .flatMap(repository::save)
                .flatMap(saved -> updateCacheSafe(saved)
                        .andThen(publishEventSafe(saved, amount))
                        .andThen(Single.just(saved))
                )
                .doOnSuccess(saved ->
                        log.info("[ChargeCreditCardUseCase] SUCCESS charge completed. creditId={}, customerId={}," +
                                        " finalBalance={}, finalAvailableBalance={}",
                                saved.id(), saved.customerId(), saved.balance(), saved.availableBalance())
                )
                .doOnError(error ->
                        log.error("[ChargeCreditCardUseCase] ERROR charge failed. creditId={}, amount={}," +
                                        " errorType={}, reason={}",
                                creditId, amount, error.getClass().getSimpleName(), error.getMessage(), error)
                );
    }

    /**
     * Loads credit using cache first, then repository.
     * Cache failures DO NOT break the flow.
     */
    /**
     * Loads credit using cache first, then repository.
     * Cache failures DO NOT break the flow.
     */
    private Single<Credit> loadCreditWithCache(String creditId) {

        return cache.getById(creditId)
                .doOnSuccess(c ->
                        log.info("[ChargeCreditCardUseCase] CACHE HIT. creditId={}, customerId={}",
                                c.id(), c.customerId())
                )
                .onErrorResumeNext(error -> {
                    log.warn("[ChargeCreditCardUseCase] CACHE ERROR (fallback to DB). creditId={}, reason={}",
                            creditId, error.getMessage());
                    return Maybe.empty();
                })
                .switchIfEmpty(
                    Maybe.defer(() ->
                        repository.findById(creditId)
                            .doOnSuccess(c ->
                                log.info("[ChargeCreditCardUseCase] DB HIT. creditId={}, customerId={}",
                                        c.id(), c.customerId())
                            )
                            .flatMap(credit ->
                                cache.save(credit)
                                    .onErrorComplete(e -> {
                                        log.warn("[ChargeCreditCardUseCase] CACHE PUT ERROR (ignored). " +
                                                        "creditId={}, reason={}",
                                            creditId, e.getMessage());
                                        return true;
                                    })
                                    .andThen(Maybe.just(credit))
                            )
                    )
                )
                .switchIfEmpty(Maybe.error(
                        new CreditNotFoundException("Credit not found: " + creditId)
                ))
                .doOnError(error ->
                        log.error("[ChargeCreditCardUseCase] ERROR searching credit. creditId={}, reason={}",
                                creditId, error.getMessage(), error)
                )
                .toSingle(); // ⭐ conversión final a Single<Credit>
    }



    /**
     * Updates cache safely. Cache failures DO NOT break the flow.
     */
    private Completable updateCacheSafe(Credit credit) {
        return cache.save(credit)
                .doOnComplete(() ->
                        log.info("[ChargeCreditCardUseCase] CACHE UPDATED creditId={}", credit.id())
                )
                .onErrorComplete(error -> {
                    log.warn("[ChargeCreditCardUseCase] CACHE UPDATE ERROR (ignored). creditId={}, reason={}",
                            credit.id(), error.getMessage());
                    return true;
                });
    }

    /**
     * Publishes event safely. Event failures DO NOT break the flow.
     */
    private Completable publishEventSafe(Credit credit, BigDecimal amount) {
        return producer.publishCreditCharged(credit, amount)
                .doOnComplete(() ->
                        log.info("[ChargeCreditCardUseCase] EVENT PUBLISHED creditId={}, amount={}",
                                credit.id(), amount)
                )
                .onErrorComplete(error -> {
                    log.error("[ChargeCreditCardUseCase] EVENT PUBLISH ERROR (ignored). creditId={}, reason={}",
                            credit.id(), error.getMessage());
                    return true;
                });
    }

    /**
     * Validates charge amount.
     */
    private Completable validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("[ChargeCreditCardUseCase] RULE_VIOLATION invalid amount. amount={}", amount);
            return Completable.error(new BusinessRuleException("Charge amount must be greater than zero"));
        }
        return Completable.complete();
    }

    /**
     * Validates credit card business rules.
     */
    private Single<Credit> validateCreditCard(Credit credit, BigDecimal amount) {

        if (credit.type() == CreditType.CREDIT_CARD) {
            log.warn("[ChargeCreditCardUseCase] RULE_VIOLATION credit type mismatch. creditId={}, " +
                            "expected=PERSONAL O BUSINESS, actual={}",
                    credit.id(), credit.type());
            return Single.error(new BusinessRuleException("Credit is credit card"));
        }

        if (credit.availableBalance().compareTo(amount) < 0) {
            log.warn("[ChargeCreditCardUseCase] LIMIT_EXCEEDED insufficient credit. creditId={}, " +
                            "availableBalance={}, requestedAmount={}",
                    credit.id(), credit.availableBalance(), amount);
            return Single.error(new BusinessRuleException("Insufficient credit limit"));
        }

        Credit updated = credit
                .withBalance(credit.balance().add(amount))
                .withAvailableBalance(credit.availableBalance().subtract(amount));

        log.debug("[ChargeCreditCardUseCase] CREDIT UPDATED IN MEMORY. creditId={}, newBalance={}, " +
                        "newAvailableBalance={}",
                updated.id(), updated.balance(), updated.availableBalance());

        return Single.just(updated);
    }
}
