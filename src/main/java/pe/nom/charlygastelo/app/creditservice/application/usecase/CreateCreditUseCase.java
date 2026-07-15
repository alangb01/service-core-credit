package pe.nom.charlygastelo.app.creditservice.application.usecase;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.domain.exception.BusinessRuleException;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;
import pe.nom.charlygastelo.app.creditservice.domain.model.CreditType;
import pe.nom.charlygastelo.app.creditservice.domain.model.Customer;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditRepositoryPort;
import pe.nom.charlygastelo.app.creditservice.domain.port.CustomerEventPort;
import pe.nom.charlygastelo.app.creditservice.domain.port.event.CreditManagementEventProducerPort;



/**
 * Caso de uso para crear solicitudes de productos crediticios.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class CreateCreditUseCase {

    private final CreditRepositoryPort repository;
    private final CreditManagementEventProducerPort producer;
    private final CustomerEventPort customerEventPort;

    public Single<Credit> execute(Credit credit) {

        log.info("Starting credit creation process for customer {}", credit.customerId());
        BigDecimal balance = credit.balance() == null ? BigDecimal.ZERO : credit.balance();
        BigDecimal limit = credit.creditLimit() == null ? BigDecimal.ZERO : credit.creditLimit();
        BigDecimal available = limit.subtract(balance);

        Instant now = Instant.now();
        Credit finalCredit = calculateDueDate(credit);

        return customerEventPort.getById(finalCredit.customerId())
                .doOnSuccess(customer ->
                        log.info("Customer {} validated successfully", customer.id())
                )
                .doOnError(error ->
                        log.error("Error validating customer {}: {}",
                                finalCredit.customerId(),
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
                            .andThen(validateCreditRules(customer, finalCredit));
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

    private Credit calculateDueDate(Credit credit) {

        Instant now = Instant.now();
        Instant nextBillingDate = null;
        Instant nextPaymentDate = null;
        Instant dueDate = null;

        switch (credit.type()) {

            case PERSONAL:
            case BUSINESS:
                // No usan ciclo
                dueDate = now.plus(30, ChronoUnit.DAYS);
                break;

            case REVOLVING:
                if (credit.billingCycleDay() != null) {

                    LocalDate today = LocalDate.now();
                    LocalDate billing = today.withDayOfMonth(credit.billingCycleDay());

                    // Si el día de corte ya pasó, se calcula para el próximo mes
                    if (billing.isBefore(today)) {
                        billing = billing.plusMonths(1);
                    }

                    nextBillingDate = billing.atStartOfDay(ZoneOffset.UTC).toInstant();

                    // Fecha de pago = corte + 20 días
                    nextPaymentDate = nextBillingDate.plus(20, ChronoUnit.DAYS);

                    // Vencimiento = fecha de pago
                    dueDate = nextPaymentDate;

                }
                else {
                    // fallback si no envían billingCycleDay
                    dueDate = now.plus(30, ChronoUnit.DAYS);
                }
                break;
        }


        return credit.withBillingInfo(nextBillingDate, nextPaymentDate, dueDate);

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
                log.error("Error validating credit rules for customer {}: {}", customer.id(),
                        "Personal customer can't create credit business");
                return Single.error(new BusinessRuleException("Personal customer can't create credit business"));
            }
        }


        log.info("No additional credit rules required for customer {}", customer.id());

        return Single.just(credit);
    }
}