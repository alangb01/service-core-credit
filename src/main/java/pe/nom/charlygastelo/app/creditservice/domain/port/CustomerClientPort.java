package pe.nom.charlygastelo.app.creditservice.domain.port;

import io.reactivex.rxjava3.core.Single;
import pe.nom.charlygastelo.app.creditservice.domain.model.Customer;

public interface CustomerClientPort {

    Single<Customer> getById(String customerId);

}