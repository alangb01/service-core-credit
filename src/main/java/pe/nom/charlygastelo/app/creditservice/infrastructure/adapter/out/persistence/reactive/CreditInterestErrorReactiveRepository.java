package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.persistence.reactive;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.persistence.document.CreditInterestErrorDocument;
import reactor.core.publisher.Flux;

public interface CreditInterestErrorReactiveRepository
        extends ReactiveMongoRepository<CreditInterestErrorDocument, String> {

    Flux<CreditInterestErrorDocument> findByCreditId(String creditId);
}
