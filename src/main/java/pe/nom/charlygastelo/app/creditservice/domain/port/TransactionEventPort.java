package pe.nom.charlygastelo.app.creditservice.domain.port;

import io.reactivex.rxjava3.core.Completable;
import pe.nom.charlygastelo.app.shared.avro.dto.TransactionCreatedEvent;

public interface TransactionEventPort {

    Completable publishTransactionCompleted(TransactionCreatedEvent event);

    Completable publishTransactionFailed(TransactionCreatedEvent event, String reason);
}