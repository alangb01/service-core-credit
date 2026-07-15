package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.persistence;

import java.time.Instant;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;
import pe.nom.charlygastelo.app.creditservice.domain.model.CreditStatus;
import pe.nom.charlygastelo.app.creditservice.domain.model.CreditType;
import pe.nom.charlygastelo.app.creditservice.domain.port.CreditRepositoryPort;


@RequiredArgsConstructor
public class CreditRepositoryAdapter implements CreditRepositoryPort {

    private final CreditReactiveRepository repository;
    private final CreditPersistenceMapper mapper;

    @Override
    public Single<Credit> save(Credit credit) {
        return Single.fromPublisher(
                repository.save(mapper.toDocument(credit))
        ).map(mapper::toDomain);
    }

    @Override
    public Maybe<Credit> findById(String id) {
        return Maybe.fromPublisher(
                repository.findById(id)
        ).map(mapper::toDomain);
    }

    @Override
    public Maybe<Credit> findByNumber(String number) {
        return Maybe.fromPublisher(
                repository.findByNumber(number)
        ).map(mapper::toDomain);
    }

    @Override
    public Flowable<Credit> findAll() {
        return Flowable.fromPublisher(
                repository.findAll()
        ).map(mapper::toDomain);
    }

    @Override
    public Flowable<Credit> findByCustomerId(String customerId) {
        return Flowable.fromPublisher(
                repository.findByCustomerId(customerId)
        ).map(mapper::toDomain);
    }

    @Override
    public Maybe<Credit> findByCustomerIdAndType(
            String customerId,
            CreditType type) {

        return Maybe.fromPublisher(
                repository.findByCustomerIdAndType(customerId, type)
        ).map(mapper::toDomain);
    }

    @Override
    public Single<Boolean> existsOverdueDebtByCustomerId(String customerId) {
        return Single.fromPublisher(
                repository.existsByCustomerIdAndOverdueTrue(customerId)
        );
    }

    @Override
    public Completable deleteById(String id) {
        return Completable.fromPublisher(
                repository.deleteById(id)
        );
    }

    @Override
    public Flowable<Credit> findAllActiveCredits() {
        return  Flowable.fromPublisher(
                repository.findByStatus(CreditStatus.ACTIVE)
                        .map(mapper::toDomain)
        );
    }

    @Override
    public Flowable<Credit> findAllCreditsWithBillingCycleDue() {
        Instant today = Instant.now();
        return Flowable.fromPublisher(
                repository.findByNextBillingDateLessThanEqual(today)
                        .map(mapper::toDomain)
        );
    }

    @Override
    public Single<Boolean> hasActiveCreditCard(String customerId) {

        return findByCustomerId(customerId)
                .filter(credit ->
                        credit.type() == CreditType.CREDIT_CARD
                                && credit.status() == CreditStatus.ACTIVE
                )
                .isEmpty()
                .map(empty -> !empty);
    }
}