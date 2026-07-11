package pe.nom.charlygastelo.app.creditservice.application.usecase;

import java.math.BigDecimal;
import io.reactivex.rxjava3.core.Completable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.nom.charlygastelo.app.creditservice.domain.port.AccountDebitEventPort;
import pe.nom.charlygastelo.app.creditservice.domain.port.TransactionEventPort;
import pe.nom.charlygastelo.app.shared.avro.dto.TransactionCreatedEvent;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProcessCreditTransactionUseCase {

    private final PayCreditUseCase payCreditUseCase;
    private final ChargeCreditCardUseCase chargeCreditCardUseCase;
    private final ChargeCreditUseCase chargeCreditUseCase;
    private final AccountDebitEventPort accountDebitEventPort;
    private final TransactionEventPort transactionEventPort;

    public Completable execute(TransactionCreatedEvent event) {
        String transactionType = event.getTransactionType().toString();

        log.info(
                "Processing credit transaction. transactionId={}, type={}, sourceProductType={}, targetProductType={}",
                event.getTransactionId(),
                transactionType,
                event.getSourceProductType(),
                event.getTargetProductType()
        );

        if ("CREDIT_PAYMENT".equalsIgnoreCase(transactionType)) {
            return payCredit(event);
        }

        if ("CREDIT_CARD_CHARGE".equalsIgnoreCase(transactionType)) {
            return chargeCreditCard(event);
        }


        if("CREDIT_WITHDRAWL".equalsIgnoreCase(transactionType)) {
            return chargeCredit(event);
        }

        log.info(
                "Transaction ignored by credit-service. transactionId={}, type={}",
                event.getTransactionId(),
                transactionType
        );

        return Completable.complete();
    }

    private Completable payCredit(TransactionCreatedEvent event) {
        String transactionId = event.getTransactionId().toString();
        String customerId = event.getCustomerId().toString();
        String accountId = event.getSourceProductId().toString();
        String creditId = event.getTargetProductId().toString();
        BigDecimal amount = BigDecimal.valueOf(event.getAmount());
        String description = event.getDescription() == null
                ? "Credit payment"
                : event.getDescription().toString();

        log.info(
                "Starting credit payment process. transactionId={}, accountId={}, creditId={}, amount={}",
                transactionId,
                accountId,
                creditId,
                amount
        );

        return accountDebitEventPort
                .debitAccount(
                        transactionId,
                        customerId,
                        accountId,
                        amount,
                        description
                )
                .flatMapCompletable(success ->
                        payCreditUseCase.execute(creditId, amount)
                                .ignoreElement()
                )
                .andThen(transactionEventPort.publishTransactionCompleted(event))
                .doOnComplete(() ->
                        log.info(
                                "Credit payment completed. transactionId={}, creditId={}, amount={}",
                                transactionId,
                                creditId,
                                amount
                        )
                )
                .onErrorResumeNext(error -> {
                    log.error(
                            "Credit payment failed. transactionId={}, creditId={}, reason={}",
                            transactionId,
                            creditId,
                            error.getMessage(),
                            error
                    );

                    return transactionEventPort
                            .publishTransactionFailed(event, error.getMessage())
                            .andThen(Completable.error(error));
                });
    }

    private Completable chargeCreditCard(TransactionCreatedEvent event) {
        String transactionId = event.getTransactionId().toString();
        String creditId = event.getTargetProductId().toString();
        BigDecimal amount = BigDecimal.valueOf(event.getAmount());

        log.info(
                "Starting credit card charge process. transactionId={}, creditId={}, amount={}",
                transactionId,
                creditId,
                amount
        );

        return chargeCreditCardUseCase.execute(creditId, amount)
                .ignoreElement()
                .andThen(transactionEventPort.publishTransactionCompleted(event))
                .doOnComplete(() ->
                        log.info(
                                "Credit card charge completed. transactionId={}, creditId={}, amount={}",
                                transactionId,
                                creditId,
                                amount
                        )
                )
                .onErrorResumeNext(error -> {
                    log.error(
                            "Credit card charge failed. transactionId={}, creditId={}, reason={}",
                            transactionId,
                            creditId,
                            error.getMessage(),
                            error
                    );

                    return transactionEventPort
                            .publishTransactionFailed(event, error.getMessage())
                            .andThen(Completable.error(error));
                });
    }

    private Completable chargeCredit(TransactionCreatedEvent event) {
        String transactionId = event.getTransactionId().toString();
        String creditId = event.getSourceProductId().toString();
        BigDecimal amount = BigDecimal.valueOf(event.getAmount());

        log.info(
                "Starting credit charge process. transactionId={}, creditId={}, amount={}",
                transactionId,
                creditId,
                amount
        );

        return chargeCreditUseCase.execute(creditId, amount)
                .ignoreElement()
                .andThen(transactionEventPort.publishTransactionCompleted(event))
                .doOnComplete(() ->
                        log.info(
                                "Credit  charge completed. transactionId={}, creditId={}, amount={}",
                                transactionId,
                                creditId,
                                amount
                        )
                )
                .onErrorResumeNext(error -> {
                    log.error(
                            "Credit  charge failed. transactionId={}, creditId={}, reason={}",
                            transactionId,
                            creditId,
                            error.getMessage(),
                            error
                    );

                    return transactionEventPort
                            .publishTransactionFailed(event, error.getMessage())
                            .andThen(Completable.error(error));
                });
    }
}