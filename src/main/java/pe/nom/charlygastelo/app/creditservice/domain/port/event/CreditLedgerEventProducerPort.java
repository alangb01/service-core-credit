package pe.nom.charlygastelo.app.creditservice.domain.port.event;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableSource;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;
import pe.nom.charlygastelo.app.creditservice.domain.model.Transaction;

public interface CreditLedgerEventProducerPort {
    Completable publishCreditPaymentOccurred(Credit credit, Transaction tx);
    Completable publishCreditWithdrawOccurred(Credit credit, Transaction tx);

    CompletableSource publishInterestChargeOccurred(Credit credit, Transaction transaction);
}