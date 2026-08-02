package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.persistence.reactive;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.persistence.document.CreditInterestLogDocument;
import reactor.core.publisher.Flux;

public interface CreditInterestLogReactiveRepository
        extends ReactiveMongoRepository<CreditInterestLogDocument, String> {

    Flux<CreditInterestLogDocument> findByCreditId(String creditId);
}
