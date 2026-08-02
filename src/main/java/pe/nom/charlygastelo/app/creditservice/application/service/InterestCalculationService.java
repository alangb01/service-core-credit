package pe.nom.charlygastelo.app.creditservice.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;
import io.reactivex.rxjava3.core.Completable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditRepositoryPort;
import pe.nom.charlygastelo.app.creditservice.domain.port.event.CreditManagementEventProducerPort;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterestCalculationService {

    private final CreditRepositoryPort repository;
    private final CreditManagementEventProducerPort producer;

    public Completable calculateDailyInterest() {
        return repository.findAllActiveCredits()
                .flatMapCompletable(credit -> calculateInterestForCredit(credit, "DAILY"));
    }

    public Completable calculateMonthlyInterest() {
        return repository.findAllActiveCredits()
                .flatMapCompletable(credit -> calculateInterestForCredit(credit, "MONTHLY"));
    }

    public Completable calculateBillingCycleInterest() {
        return repository.findAllCreditsWithBillingCycleDue()
                .flatMapCompletable(credit -> calculateInterestForCredit(credit, "BILLING"));
    }

    private Completable calculateInterestForCredit(Credit credit, String cycle) {

        BigDecimal interest = credit.balance()
                .multiply(credit.interestRate())
                .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);

        log.info("[InterestCalculation] creditId={}, cycle={}, interest={}",
                credit.id(), cycle, interest);

        return producer.publishInterestCalculated(credit, interest, cycle)
                .andThen(Completable.complete());
    }
}
