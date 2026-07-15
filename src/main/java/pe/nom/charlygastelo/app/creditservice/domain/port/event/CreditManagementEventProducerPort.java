package pe.nom.charlygastelo.app.creditservice.domain.port.event;

import java.math.BigDecimal;
import io.reactivex.rxjava3.core.Completable;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;

public interface CreditManagementEventProducerPort {

    Completable publishCreditCreated(Credit credit);

    Completable publishCreditUpdated(Credit credit);

    Completable publishCreditOverdue(Credit credit);

    Completable publishCreditDeleted(Credit credit);

    Completable publishInterestCalculated(Credit credit, BigDecimal interest, String Cycle);

}