package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.event;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.creditservice.domain.model.Customer;
import pe.nom.charlygastelo.app.creditservice.domain.port.CustomerEventPort;
import pe.nom.charlygastelo.app.shared.avro.dto.CustomerRequestEvent;


@Component
@RequiredArgsConstructor
public class CustomerKafkaClient implements CustomerEventPort {

    private final CustomerRequestProducer producer;
    private final CustomerResponseRegistry registry;

    @Override
    public Single<Customer> getById(String customerId) {
        String correlationId = UUID.randomUUID().toString();

        CustomerRequestEvent event = CustomerRequestEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setEventType("CUSTOMER_REQUEST")
                .setOccurredAt(Instant.now().toString())
                .setVersion("1.0")
                .setSource("credit-service")
                .setCorrelationId(correlationId)
                .setCustomerId(customerId)
                .build();

        return registry.waitForResponse(correlationId)
                .doOnSubscribe(d -> producer.send(correlationId, event));
    }
}