package pe.nom.charlygastelo.app.creditservice.application.usecase;

import java.math.BigDecimal;

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
        return repository.findById(creditId)
                .switchIfEmpty(Single.error(new CreditNotFoundException("Credit not found")))
                .flatMap(credit -> {
                    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        return Single.error(new BusinessRuleException("Payment amount must be greater than zero"));
                    }

                    BigDecimal newBalance = credit.balance().subtract(amount);

                    if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                        newBalance = BigDecimal.ZERO;
                    }

                    Credit updated = credit.withBalance(newBalance);

                    return repository.save(updated);
                })
                .flatMap(saved ->
                        producer.publishCreditPaid(saved)
                                .andThen(Single.just(saved))
                );
    }
}