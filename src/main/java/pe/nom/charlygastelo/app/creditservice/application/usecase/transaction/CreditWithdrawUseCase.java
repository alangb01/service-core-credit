package pe.nom.charlygastelo.app.creditservice.application.usecase.transaction;

import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.domain.exception.BusinessRuleException;
import pe.nom.charlygastelo.app.creditservice.domain.model.ProcessedTransaction;
import pe.nom.charlygastelo.app.creditservice.domain.model.Transaction;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditRepositoryPort;
import pe.nom.charlygastelo.app.creditservice.domain.port.event.CreditLedgerEventProducerPort;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.event.TransactionEventProducer;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreditWithdrawUseCase {
    private final CreditRepositoryPort creditRepository;
    private final CreditLedgerEventProducerPort creditProducer;
    private final TransactionEventProducer transactionProducer;

    public Completable execute(Transaction tx) {
        log.info("[CREDIT] Executing CREDIT_WITHDRAW use case for txId={} amount={}",
                tx.id(), tx.amount());

        return Single.fromCallable(() -> {
                    tx.validateForWithdraw();
                    return tx;
                })
                .flatMap(transaction ->
                        creditRepository.findById(transaction.sourceProductId())
                                .switchIfEmpty(Single.error(new BusinessRuleException("Credit not found")))
                                .map(credit -> credit.debit(transaction.amount()))
                                .flatMap(creditRepository::save)
                                .map(creditSaved -> new ProcessedTransaction(transaction, creditSaved, null))
                )
                .flatMapCompletable(processed ->
                        creditProducer.publishCreditWithdrawOccurred(
                                processed.credit(),
                                processed.transaction()
                        )
                )
                .doOnComplete(() ->
                        log.info("[CREDIT] CREDIT_WITHDRAW completed for txId={}", tx.id())
                )
                .onErrorResumeNext(err -> {
                    log.error("[CREDIT] CREDIT_WITHDRAW failed for txId={} reason={}",
                            tx.id(), err.getMessage());

                    return transactionProducer.publishTransactionFailed(tx.id(), tx.customerId(), err.getMessage())
                            .andThen(Completable.error(err));
                });
    }
}
