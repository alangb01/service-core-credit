package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.rest.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;
import pe.nom.charlygastelo.app.creditservice.domain.model.CreditStatus;
import pe.nom.charlygastelo.app.creditservice.domain.model.CreditType;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.rest.request.CreateCreditRequest;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.rest.request.UpdateCreditRequest;
import pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.in.rest.response.CreditResponse;

@Component
public class CreditRestMapper {

    public Credit toDomain(CreateCreditRequest request) {
        BigDecimal balance = request.balance() == null
                ? BigDecimal.ZERO
                : request.balance();

        BigDecimal creditLimit = request.creditLimit() == null
                ? BigDecimal.ZERO
                : request.creditLimit();

        return new Credit(
                null,
                request.customerId(),
                request.number(),
                CreditType.valueOf(request.type()),
                CreditStatus.ACTIVE,
                creditLimit,
                balance,
                creditLimit.subtract(balance),
                request.interestRate(),
                request.installments(),
                request.dueDate(),
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public Credit toDomain(String id, UpdateCreditRequest request) {
        return new Credit(
                id,
                null,
                request.number(),
                CreditType.valueOf(request.type()),
                CreditStatus.valueOf(request.status()),
                request.creditLimit(),
                request.balance(),
                request.availableBalance(),
                request.interestRate(),
                request.installments(),
                request.dueDate(),
                request.overdue(),
                null,
                LocalDateTime.now()
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
                credit.availableBalance(),
                credit.interestRate(),
                credit.installments(),
                credit.dueDate(),
                credit.overdue(),
                credit.createdAt(),
                credit.updatedAt()
        );
    }
}