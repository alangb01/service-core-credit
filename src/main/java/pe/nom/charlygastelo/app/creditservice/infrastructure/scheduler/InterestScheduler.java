package pe.nom.charlygastelo.app.creditservice.infrastructure.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.application.service.InterestCalculationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class InterestScheduler {

    private final InterestCalculationService interestService;

    /**
     * Ejecuta el cálculo de intereses todos los días a las 23:00.
     */
    @Scheduled(cron = "0 0 23 * * ?")
    public void runDailyInterestCalculation() {
        log.info("[InterestScheduler] Running daily interest calculation...");
        interestService.calculateDailyInterest()
                .doOnComplete(() -> log.info("[InterestScheduler] Daily interest calculation completed"))
                .doOnError(error -> log.error("[InterestScheduler] Error calculating daily interest: {}", error.getMessage()))
                .subscribe();
    }

    /**
     * Ejecuta el cálculo de intereses mensuales el día 1 a las 00:00.
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void runMonthlyInterestCalculation() {
        log.info("[InterestScheduler] Running monthly interest calculation...");
        interestService.calculateMonthlyInterest()
                .doOnComplete(() -> log.info("[InterestScheduler] Monthly interest calculation completed"))
                .doOnError(error -> log.error("[InterestScheduler] Error calculating monthly interest: {}", error.getMessage()))
                .subscribe();
    }

    /**
     * Ejecuta el ciclo de facturación de tarjetas de crédito.
     * Ejemplo: todos los días a las 02:00.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void runBillingCycleInterest() {
        log.info("[InterestScheduler] Running billing cycle interest calculation...");
        interestService.calculateBillingCycleInterest()
                .doOnComplete(() -> log.info("[InterestScheduler] Billing cycle interest calculation completed"))
                .doOnError(error -> log.error("[InterestScheduler] Error calculating billing cycle interest: {}", error.getMessage()))
                .subscribe();
    }
}
