package pe.nom.charlygastelo.app.creditservice.domain.port;

import io.reactivex.rxjava3.core.Single;

public interface OverdueDebtEventPort {

    Single<Boolean> hasOverdueDebt(String customerId);

}