package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.rest;



import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.creditservice.application.usecase.*;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.rest.mapper.CreditRestMapper;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.rest.request.*;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.rest.response.CreditResponse;

@RestController
@RequestMapping("/credits")
@RequiredArgsConstructor
public class CreditController {

    private final CreateCreditUseCase createCreditUseCase;
    private final GetCreditUseCase getCreditUseCase;
    private final ListCreditsUseCase listCreditsUseCase;
    private final UpdateCreditUseCase updateCreditUseCase;
    private final DeleteCreditUseCase deleteCreditUseCase;
    private final PayCreditUseCase payCreditUseCase;
    private final ChargeCreditCardUseCase chargeCreditCardUseCase;
    private final CheckOverdueDebtUseCase checkOverdueDebtUseCase;
    private final CreditRestMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Single<CreditResponse> create(@RequestBody CreateCreditRequest request) {
        return createCreditUseCase.execute(mapper.toDomain(request))
                .map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    public Single<CreditResponse> findById(@PathVariable String id) {
        return getCreditUseCase.byId(id)
                .map(mapper::toResponse)
                .toSingle();
    }

    @GetMapping
    public Flowable<CreditResponse> findAll() {
        return listCreditsUseCase.all()
                .map(mapper::toResponse);
    }

    @GetMapping("/customer/{customerId}")
    public Flowable<CreditResponse> findByCustomer(@PathVariable String customerId) {
        return listCreditsUseCase.byCustomer(customerId)
                .map(mapper::toResponse);
    }

    @PutMapping("/{id}")
    public Single<CreditResponse> update(
            @PathVariable String id,
            @RequestBody UpdateCreditRequest request) {

        return updateCreditUseCase.execute(id, mapper.toDomain(id, request))
                .map(mapper::toResponse);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Completable delete(@PathVariable String id) {
        return deleteCreditUseCase.execute(id);
    }

    @PostMapping("/{id}/payments")
    public Single<CreditResponse> pay(
            @PathVariable String id,
            @RequestBody CreditPaymentRequest request) {

        return payCreditUseCase.execute(id, request.amount())
                .map(mapper::toResponse);
    }

    @PostMapping("/{id}/charges")
    public Single<CreditResponse> charge(
            @PathVariable String id,
            @RequestBody CreditChargeRequest request) {

        return chargeCreditCardUseCase.execute(id, request.amount())
                .map(mapper::toResponse);
    }

    @GetMapping("/customer/{customerId}/overdue-debt")
    public Single<Boolean> hasOverdueDebt(@PathVariable String customerId) {
        return checkOverdueDebtUseCase.execute(customerId);
    }
}