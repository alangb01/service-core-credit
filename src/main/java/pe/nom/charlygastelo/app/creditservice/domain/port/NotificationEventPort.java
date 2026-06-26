package pe.nom.charlygastelo.app.creditservice.domain.port;


import io.reactivex.rxjava3.core.Completable;

public interface NotificationEventPort {

    Completable notifyCreditCreated(String customerId);

    Completable notifyPayment(String customerId);

    Completable notifyOverdueDebt(String customerId);

}