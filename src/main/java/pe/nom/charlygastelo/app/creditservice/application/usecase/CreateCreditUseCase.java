package pe.nom.charlygastelo.app.creditservice.application.usecase;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.domain.exception.BusinessRuleException;
import pe.nom.charlygastelo.app.creditservice.domain.exception.CustomerInactiveException;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;
import pe.nom.charlygastelo.app.creditservice.domain.model.CreditType;
import pe.nom.charlygastelo.app.creditservice.domain.model.Customer;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditEventProducerPort;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditRepositoryPort;
import pe.nom.charlygastelo.app.creditservice.domain.port.CustomerEventPort;

/**
 * Caso de uso para crear solicitudes de productos crediticios.
 */
@Slf4j
@RequiredArgsConstructor
public class CreateCreditUseCase {

    private final CreditRepositoryPort repository;
    private final CreditEventProducerPort producer;
    private final CustomerEventPort customerEventPort;

    public Single<Credit> execute(Credit credit) {

        log.info("Starting credit creation process for customer {}", credit.customerId());

        return customerEventPort.getById(credit.customerId())
                .doOnSuccess(customer ->
                        log.info("Customer {} validated successfully", customer.id())
                )
                .doOnError(error ->
                        log.error("Error validating customer {}: {}",
                                credit.customerId(),
                                error.getMessage(),
                                error)
                )
                .flatMap(customer -> {
                    if (!customer.active()) {
                        log.warn("Customer {} is inactive", customer.id());
                        return Single.error(
                                new BusinessRuleException("Customer is inactive")
                        );
                    }

                    return validateNoOverdueDebt(credit.customerId())
                            .andThen(validateCreditRules(customer, credit));
                })
                .flatMap(repository::save)
                .doOnSuccess(saved ->
                        log.info("Credit {} created successfully", saved.id())
                )
                .doOnError(error ->
                        log.error("Error saving credit for customer {}: {}",
                                credit.customerId(),
                                error.getMessage(),
                                error)
                )
                .flatMap(saved ->
                        producer.publishCreditCreated(saved)
                                .doOnComplete(() ->
                                        log.info("CreditCreatedEvent published for credit {}", saved.id())
                                )
                                .doOnError(error ->
                                        log.error("Error publishing CreditCreatedEvent for credit {}: {}",
                                                saved.id(),
                                                error.getMessage(),
                                                error)
                                )
                                .andThen(Single.just(saved))
                );
    }

    private Completable validateNoOverdueDebt(String customerId) {

        log.info("Validating overdue debt for customer {}", customerId);

        return repository.existsOverdueDebtByCustomerId(customerId)
                .doOnSuccess(hasDebt ->
                        log.debug("Overdue debt validation. customer={}, hasDebt={}",
                                customerId,
                                hasDebt)
                )
                .flatMapCompletable(hasDebt -> {
                    if (hasDebt) {
                        log.warn("Customer {} has overdue debt", customerId);

                        return Completable.error(
                                new BusinessRuleException("Customer has overdue debt")
                        );
                    }

                    log.info("Customer {} has no overdue debt", customerId);

                    return Completable.complete();
                })
                .doOnError(error ->
                        log.error("Error validating overdue debt for customer {}: {}",
                                customerId,
                                error.getMessage(),
                                error)
                );
    }

    private Single<Credit> validateCreditRules(Customer customer, Credit credit) {

        log.info("Validating credit rules. customer={}, type={}",
                customer.id(),
                credit.type());

        if (customer.isPersonal()) {
            if (credit.type() == CreditType.PERSONAL) {

                return repository.findByCustomerIdAndType(
                                customer.id(), CreditType.PERSONAL
                        )
                        .isEmpty()
                        .doOnSuccess(isEmpty ->
                                log.debug("Existing personal credit validation. customer={}, available={}",
                                        customer.id(),
                                        isEmpty)
                        )
                        .flatMap(isEmpty -> {
                            if (!isEmpty) {

                                log.warn("Customer {} already has a personal credit",
                                        customer.id());

                                return Single.error(
                                        new BusinessRuleException(
                                                "Personal customer can have only one personal credit"
                                        )
                                );
                            }

                            log.info("Credit rules validated successfully for customer {}",
                                    customer.id());

                            return Single.just(credit);
                        })
                        .doOnError(error ->
                                log.error("Error validating credit rules for customer {}: {}",
                                        customer.id(),
                                        error.getMessage(),
                                        error)
                        );
            }

            if (credit.type() == CreditType.BUSINESS) {
                log.error("Error validating credit rules for customer {}: {}",customer.id(),"Personal customer can't create credit business");
                return Single.error(new BusinessRuleException("Personal customer can't create credit business"));
            }
        }


        log.info("No additional credit rules required for customer {}", customer.id());

        return Single.just(credit);
    }
}