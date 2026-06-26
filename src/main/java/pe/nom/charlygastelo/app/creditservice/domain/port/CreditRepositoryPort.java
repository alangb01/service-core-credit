package pe.nom.charlygastelo.app.creditservice.domain.port;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;
import pe.nom.charlygastelo.app.creditservice.domain.model.CreditType;


public interface CreditRepositoryPort {

    Single<Credit> save(Credit credit);

    Maybe<Credit> findById(String id);

    Maybe<Credit> findByNumber(String number);

    Flowable<Credit> findAll();

    Flowable<Credit> findByCustomerId(String customerId);

    Maybe<Credit> findByCustomerIdAndType(String customerId, CreditType type);

    Single<Boolean> existsOverdueDebtByCustomerId(String customerId);

    Completable deleteById(String id);
}