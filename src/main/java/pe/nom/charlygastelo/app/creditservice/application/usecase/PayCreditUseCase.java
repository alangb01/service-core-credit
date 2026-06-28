package pe.nom.charlygastelo.app.creditservice.application.usecase;

import java.math.BigDecimal;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.domain.exception.BusinessRuleException;
import pe.nom.charlygastelo.app.creditservice.domain.exception.CreditNotFoundException;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditEventProducerPort;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditRepositoryPort;

@RequiredArgsConstructor
@Slf4j
public class PayCreditUseCase {

    private final CreditRepositoryPort repository;
    private final CreditEventProducerPort producer;

    public Single<Credit> execute(String creditId, BigDecimal amount) {

        log.info("Starting credit payment. creditId={}, amount={}",
                creditId, amount);

        return validateAmount(amount)
                .andThen(
                        repository.findById(creditId)
                                .switchIfEmpty(
                                        Single.error(new CreditNotFoundException(
                                                "Credit not found: " + creditId))
                                )
                )
                .flatMap(credit -> {

                    BigDecimal payment =
                            amount.min(credit.balance());

                    Credit updated =
                            credit.withBalance(
                                    credit.balance().subtract(payment)
                            );

                    return repository.save(updated)
                            .flatMap(saved ->
                                    producer.publishCreditPaid(saved, payment)
                                            .doOnComplete(() ->
                                                    log.info(
                                                            "CreditPaidEvent published. creditId={}, amount={}",
                                                            saved.id(),
                                                            payment
                                                    )
                                            )
                                            .andThen(Single.just(saved))
                            );

                })
                .doOnSuccess(saved ->
                        log.info(
                                "Credit payment completed successfully. creditId={}, remainingBalance={}",
                                saved.id(),
                                saved.balance()
                        )
                )
                .doOnError(error ->
                        log.error(
                                "Error paying credit. creditId={}, amount={}, reason={}",
                                creditId,
                                amount,
                                error.getMessage(),
                                error
                        )
                );
    }

    private Completable validateAmount(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Completable.error(
                    new BusinessRuleException(
                            "Payment amount must be greater than zero"
                    )
            );
        }

        return Completable.complete();
    }
}