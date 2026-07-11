package pe.nom.charlygastelo.app.creditservice.domain.port;

import java.math.BigDecimal;
import io.reactivex.rxjava3.core.Completable;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;

public interface CreditEventProducerPort {

    Completable publishCreditCreated(Credit credit);

    Completable publishCreditUpdated(Credit credit);

    Completable publishCreditPaid(Credit credit, BigDecimal amount);

    Completable publishCreditCharged(Credit credit, BigDecimal amount);

    Completable publishCreditOverdue(Credit credit);

    Completable publishCreditDeleted(Credit credit);
}