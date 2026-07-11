package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import pe.nom.charlygastelo.app.creditservice.domain.exception.CustomerNotFoundException;
import pe.nom.charlygastelo.app.creditservice.domain.model.Customer;
import pe.nom.charlygastelo.app.shared.avro.dto.CustomerResponseEvent;

@Component
public class CustomerResponseRegistry {

    @Value("${customer.response.timeout:30}")
    private int timeout;

    private final Map<String, SingleEmitter<Customer>> pendingRequests =
            new ConcurrentHashMap<>();

    public Single<Customer> waitForResponse(String correlationId) {
        return Single.<Customer>create(emitter ->
                        pendingRequests.put(correlationId, emitter)
                ).timeout(timeout, TimeUnit.SECONDS)
                .doFinally(() -> pendingRequests.remove(correlationId));
    }

    public void complete(CustomerResponseEvent event) {
        SingleEmitter<Customer> emitter =
                pendingRequests.remove(event.getCorrelationId().toString());

        if (emitter == null) return;

        if (!event.getFound()) {
            emitter.onError(new CustomerNotFoundException("Customer not found"));
            return;
        }

        emitter.onSuccess(new Customer(
                event.getCustomerId().toString(),
                event.getCustomerType().toString(),
                event.getDocumentType().toString(),
                event.getDocumentNumber().toString(),
                event.getName().toString(),
                event.getLastName().toString(),
                event.getEmail().toString(),
                event.getPhone().toString(),
                event.getActive()
        ));
    }
}