package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.persistence;

import org.springframework.stereotype.Component;
import pe.nom.charlygastelo.app.creditservice.domain.model.Credit;

@Component
public class CreditPersistenceMapper {

    public CreditDocument toDocument(Credit domain) {

        return CreditDocument.builder()
                .id(domain.id())
                .customerId(domain.customerId())
                .productId(domain.productId())
                .number(domain.number())
                .type(domain.type())
                .status(domain.status())
                .creditLimit(domain.creditLimit())
                .balance(domain.balance())
                .available(domain.available())
                .interestRate(domain.interestRate())
                .billingCycleDay(domain.billingCycleDay())
                .nextBillingDate(domain.nextBillingDate())
                .nextPaymentDate(domain.nextPaymentDate())
                .installments(domain.installments())
                .dueDate(domain.dueDate())
                .overdue(domain.overdue())
                .createdAt(domain.createdAt())
                .updatedAt(domain.updatedAt())
                .build();
    }

    public Credit toDomain(CreditDocument document) {

        return new Credit(
                document.getId(),
                document.getCustomerId(),
                document.getProductId(),
                document.getNumber(),
                document.getType(),
                document.getStatus(),
                document.getCreditLimit(),
                document.getBalance(),
                document.getAvailable(),
                document.getInterestRate(),
                document.getBillingCycleDay(),
                document.getNextBillingDate(),
                document.getNextPaymentDate(),

                document.getInstallments(),
                document.getDueDate(),
                document.isOverdue(),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }

}