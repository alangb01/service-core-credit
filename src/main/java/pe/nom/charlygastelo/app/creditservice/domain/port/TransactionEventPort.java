package pe.nom.charlygastelo.app.creditservice.domain.port;

import io.reactivex.rxjava3.core.Completable;

public interface TransactionEventPort {

    Completable publishTransactionFailed(String transactionId, String customerId, String reason);
}