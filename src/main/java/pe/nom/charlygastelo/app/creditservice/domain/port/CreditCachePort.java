package pe.nom.charlygastelo.app.creditservice.domain.port;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;

public interface CreditCachePort {
    Maybe<Credit> getById(String id);


    Completable save(Credit account);

    Completable delete(String id);
}
