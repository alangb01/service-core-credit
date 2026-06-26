package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.persistence;


import org.springframework.stereotype.Repository;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;
import pe.nom.charlygastelo.app.creditservice.domain.model.CreditType;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditRepositoryPort;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.persistence.CreditPersistenceMapper;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.persistence.CreditReactiveRepository;
import reactor.adapter.rxjava.RxJava3Adapter;

@Repository
@RequiredArgsConstructor
public class CreditRepositoryAdapter implements CreditRepositoryPort {

    private final CreditReactiveRepository repository;
    private final CreditPersistenceMapper mapper;

    @Override
    public Single<Credit> save(Credit credit) {
        return RxJava3Adapter.monoToSingle(
                repository.save(mapper.toDocument(credit))
        ).map(mapper::toDomain);
    }

    @Override
    public Maybe<Credit> findById(String id) {
        return RxJava3Adapter.monoToMaybe(
                repository.findById(id)
        ).map(mapper::toDomain);
    }

    @Override
    public Maybe<Credit> findByNumber(String number) {
        return RxJava3Adapter.monoToMaybe(
                repository.findByNumber(number)
        ).map(mapper::toDomain);
    }

    @Override
    public Flowable<Credit> findAll() {
        return RxJava3Adapter.fluxToFlowable(
                repository.findAll()
        ).map(mapper::toDomain);
    }

    @Override
    public Flowable<Credit> findByCustomerId(String customerId) {
        return RxJava3Adapter.fluxToFlowable(
                repository.findByCustomerId(customerId)
        ).map(mapper::toDomain);
    }

    @Override
    public Maybe<Credit> findByCustomerIdAndType(
            String customerId,
            CreditType type) {

        return RxJava3Adapter.monoToMaybe(
                repository.findByCustomerIdAndType(customerId, type)
        ).map(mapper::toDomain);
    }

    @Override
    public Single<Boolean> existsOverdueDebtByCustomerId(String customerId) {
        return RxJava3Adapter.monoToSingle(
                repository.existsByCustomerIdAndOverdueTrue(customerId)
        );
    }

    @Override
    public Completable deleteById(String id) {
        return RxJava3Adapter.monoToCompletable(
                repository.deleteById(id)
        );
    }
}