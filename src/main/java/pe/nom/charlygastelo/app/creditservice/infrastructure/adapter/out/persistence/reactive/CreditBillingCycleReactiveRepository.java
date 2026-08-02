package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.persistence.reactive;

import java.time.Instant;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.persistence.document.CreditBillingCycleDocument;
import reactor.core.publisher.Flux;


public interface CreditBillingCycleReactiveRepository
        extends ReactiveMongoRepository<CreditBillingCycleDocument, String> {

    Flux<CreditBillingCycleDocument> findByCreditId(String creditId);

    Flux<CreditBillingCycleDocument> findByCycleEndLessThanEqual(Instant date);
}
