package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.rest;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.reactivex.rxjava3.core.Single;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import pe.nom.charlygastelo.app.creditservice.application.usecase.CheckOverdueDebtUseCase;
import pe.nom.charlygastelo.app.creditservice.application.usecase.ListCreditsUseCase;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.rest.dto.response.CreditResponse;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.rest.dto.response.OverdueDebtResponse;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.rest.mapper.CreditRestMapper;

@RestController
@RequestMapping("/customers/{customerId}/credits")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CustomerController {
    private final ListCreditsUseCase listCreditsUseCase;
    private final CheckOverdueDebtUseCase checkOverdueDebtUseCase;
    private final CreditRestMapper restMapper;

    @GetMapping
    public Single<List<CreditResponse>> listByCustomer(
            @PathVariable String id) {

        return listCreditsUseCase.findByCustomerId(id)
                .map(list -> list.stream().map(restMapper::toResponse).toList());
    }

    @GetMapping("/overdue")
    public Single<OverdueDebtResponse> hasOverdueDebt(@PathVariable String customerId) {

        return checkOverdueDebtUseCase.execute(customerId)
                .map(OverdueDebtResponse::new);
    }
}