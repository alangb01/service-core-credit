package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import pe.nom.charlygastelo.app.creditservice.domain.model.CreditType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditReactiveRepository
        extends ReactiveMongoRepository<CreditDocument, String> {

    Mono<CreditDocument> findByNumber(String number);

    Flux<CreditDocument> findByCustomerId(String customerId);

    Mono<CreditDocument> findByCustomerIdAndType(
            String customerId,
            CreditType type
    );

    Mono<Boolean> existsByCustomerIdAndOverdueTrue(String customerId);

}