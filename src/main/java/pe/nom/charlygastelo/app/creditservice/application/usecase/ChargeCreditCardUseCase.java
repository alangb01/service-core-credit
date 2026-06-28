package pe.nom.charlygastelo.app.creditservice.application.usecase;

import java.math.BigDecimal;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.domain.exception.BusinessRuleException;
import pe.nom.charlygastelo.app.creditservice.domain.exception.CreditNotFoundException;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;
import pe.nom.charlygastelo.app.creditservice.domain.model.CreditType;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditEventProducerPort;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditRepositoryPort;

@RequiredArgsConstructor
@Slf4j
public class ChargeCreditCardUseCase {

    private final CreditRepositoryPort repository;
    private final CreditEventProducerPort producer;

    public Single<Credit> execute(String creditId, BigDecimal amount) {
        log.info("Starting credit card charge. creditId={}, amount={}", creditId, amount);

        return validateAmount(amount)
                .andThen(repository.findById(creditId)
                        .switchIfEmpty(Single.error(
                                new CreditNotFoundException("Credit not found: " + creditId)
                        ))
                )
                .flatMap(credit -> validateCreditCard(credit, amount))
                .flatMap(repository::save)
                .flatMap(saved ->
                        producer.publishCreditCharged(saved, amount)
                                .doOnComplete(() ->
                                        log.info("CreditChargedEvent published. creditId={}, amount={}",
                                                saved.id(), amount))
                                .andThen(Single.just(saved))
                )
                .doOnSuccess(saved ->
                        log.info("Credit card charge completed. creditId={}, balance={}, availableBalance={}",
                                saved.id(), saved.balance(), saved.availableBalance()))
                .doOnError(error ->
                        log.error("Error charging credit card. creditId={}, amount={}, reason={}",
                                creditId, amount, error.getMessage(), error));
    }

    private Completable validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Completable.error(
                    new BusinessRuleException("Charge amount must be greater than zero")
            );
        }

        return Completable.complete();
    }

    private Single<Credit> validateCreditCard(Credit credit, BigDecimal amount) {
        if (credit.type() != CreditType.CREDIT_CARD) {
            return Single.error(new BusinessRuleException("Credit is not a credit card"));
        }

        if (credit.availableBalance().compareTo(amount) < 0) {
            return Single.error(new BusinessRuleException("Insufficient credit limit"));
        }

        Credit updated = credit
                .withBalance(credit.balance().add(amount))
                .withAvailableBalance(credit.availableBalance().subtract(amount));

        return Single.just(updated);
    }
}