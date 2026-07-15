package pe.nom.charlygastelo.app.creditservice.application.usecase.transaction;

import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Completable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.nom.charlygastelo.app.creditservice.domain.model.Transaction;
import pe.nom.charlygastelo.app.creditservice.domain.model.TransactionType;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreatedTransactionUseCase {

    private final CreditWithdrawUseCase creditWithdrawUseCase;
    private final CreditPaymentUseCase creditPaymentUseCase;
    private final CreditInterestUseCase creditInterestUseCase;

    public boolean isCreditServiceResponsible(TransactionType type) {
        return switch (type) {
            case CREDIT_WITHDRAW,
                 CREDIT_PAYMENT,
                 CREDIT_PAYMENT_THIRD,
                 CREDIT_INTEREST -> true;
            default -> false;
        };
    }

    public Completable execute(Transaction tx) {
        log.info("[CREDIT] Routing txId={} type={} to correct use case", tx.id(), tx.type());

        return switch (tx.type()) {
            case CREDIT_WITHDRAW -> creditWithdrawUseCase.execute(tx);

            case CREDIT_PAYMENT, CREDIT_PAYMENT_THIRD -> creditPaymentUseCase.execute(tx);

            case CREDIT_INTEREST -> creditInterestUseCase.execute(tx);

            default -> {
                log.warn("[CREDIT] txId={} type={} ignored (not credit responsibility)", tx.id(), tx.type());
                yield Completable.complete();
            }
        };
    }
}
