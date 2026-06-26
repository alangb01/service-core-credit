package pe.nom.charlygastelo.app.creditservice.application.usecase;

import java.math.BigDecimal;

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
        return repository.findById(creditId)
                .switchIfEmpty(Single.error(new CreditNotFoundException("Credit not found")))
                .flatMap(credit -> {
                    if (credit.type() != CreditType.CREDIT_CARD) {
                        return Single.error(new BusinessRuleException("Credit is not a credit card"));
                    }

                    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        return Single.error(new BusinessRuleException("Charge amount must be greater than zero"));
                    }

                    if (credit.availableBalance().compareTo(amount) < 0) {
                        return Single.error(new BusinessRuleException("Insufficient credit limit"));
                    }

                    Credit updated = credit
                            .withBalance(credit.balance().add(amount))
                            .withAvailableBalance(credit.availableBalance().subtract(amount));

                    return repository.save(updated);
                })
                .flatMap(saved ->
                        producer.publishCreditCharged(saved)
                                .andThen(Single.just(saved))
                );
    }
}