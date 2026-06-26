package pe.nom.charlygastelo.app.creditservice.domain.port;

import io.reactivex.rxjava3.core.Completable;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;

public interface CreditEventProducerPort {

    Completable publishCreditCreated(Credit credit);

    Completable publishCreditUpdated(Credit credit);

    Completable publishCreditPaid(Credit credit);

    Completable publishCreditCharged(Credit credit);

    Completable publishCreditOverdue(Credit credit);

    Completable publishCreditDeleted(Credit credit);

}