package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.rest.mapper;

import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.stereotype.Component;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;
import pe.nom.charlygastelo.app.creditservice.domain.model.CreditStatus;
import pe.nom.charlygastelo.app.creditservice.domain.model.CreditType;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.rest.dto.request.CreateCreditRequest;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.rest.dto.request.UpdateCreditRequest;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.rest.dto.response.CreditResponse;

@Component
public class CreditRestMapper {

    public Credit toDomain(CreateCreditRequest request) {
        BigDecimal balance = request.balance() == null
                ? BigDecimal.ZERO
                : request.balance();

        BigDecimal creditLimit = request.creditLimit() == null
                ? BigDecimal.ZERO
                : request.creditLimit();

        BigDecimal available = creditLimit.subtract(balance);


        return new Credit(
                null,
                request.customerId(),
                null,
                request.number(),
                CreditType.valueOf(request.type()),
                CreditStatus.ACTIVE,
                creditLimit,
                balance,
                available,
                request.interestRate(),
                request.billingCycleDay(),
                null,
                null,
                request.installments(),
                null,
                false,
                Instant.now(),
                null
        );
    }

    public Credit toDomain(String id, UpdateCreditRequest request) {
        return new Credit(
                id,
                null,
                null,
                request.number(),
                CreditType.valueOf(request.type()),
                CreditStatus.valueOf(request.status()),
                request.creditLimit(),
                request.balance(),
                request.availableBalance(),
                request.interestRate(),
                null,
                null,
                null,
                request.installments(),
                request.dueDate(),
                request.overdue(),
                null,
                Instant.now()
        );
    }

    public CreditResponse toResponse(Credit credit) {
        return new CreditResponse(
                credit.id(),
                credit.customerId(),
                credit.number(),
                credit.type().name(),
                credit.status().name(),
                credit.creditLimit(),
                credit.balance(),
                credit.available(),
                credit.interestRate(),
                credit.billingCycleDay(),
                credit.nextBillingDate(),
                credit.nextPaymentDate(),
                credit.installments(),
                credit.dueDate(),
                credit.overdue(),
                credit.createdAt(),
                credit.updatedAt()
        );
    }
}