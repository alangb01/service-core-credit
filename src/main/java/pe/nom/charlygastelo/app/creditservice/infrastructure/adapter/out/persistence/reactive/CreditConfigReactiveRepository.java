package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.persistence.reactive;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.persistence.document.CreditConfigDocument;

public interface CreditConfigReactiveRepository
        extends ReactiveMongoRepository<CreditConfigDocument, String> {
}
