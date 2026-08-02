package pe.nom.charlygastelo.app.creditservice.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import pe.nom.charlygastelo.app.creditservice.domain.exception.BusinessRuleException;

public record Credit(
        String id,
        String customerId,
        String productId,
        String number,
        CreditType type,
        CreditStatus status,
        BigDecimal creditLimit,
        BigDecimal balance,
        BigDecimal available,
        BigDecimal interestRate,
        Integer billingCycleDay,     // día de corte
        Instant nextBillingDate,     // fecha de corte
        Instant nextPaymentDate,     // fecha de pago
        Integer installments,
        Instant dueDate,
        boolean overdue,
        Instant createdAt,
        Instant updatedAt

) {

    public Credit withBalance(BigDecimal balance) {
        return new Credit(
                id,
                customerId,
                productId,
                number,
                type,
                status,
                creditLimit,
                balance,
                creditLimit.subtract(balance),
                interestRate,
                billingCycleDay,
                nextBillingDate,
                nextPaymentDate,
                installments,
                dueDate,
                overdue,
                createdAt,
                Instant.now()
        );
    }

    public Credit withAvailableBalance(BigDecimal available) {
        return new Credit(
                id,
                customerId,
                productId,
                number,
                type,
                status,
                creditLimit,
                balance,
                available,
                interestRate,
                billingCycleDay,
                nextBillingDate,
                nextPaymentDate,
                installments,
                dueDate,
                overdue,
                createdAt,
                Instant.now()
        );
    }

    public Credit withStatus(CreditStatus status) {
        return new Credit(
                id,
                customerId,
                productId,
                number,
                type,
                status,
                creditLimit,
                balance,
                available,
                interestRate,
                billingCycleDay,
                nextBillingDate,
                nextPaymentDate,
                installments,
                dueDate,
                overdue,
                createdAt,
                Instant.now()
        );
    }


    public Credit withId(String id) {
        return new Credit(
                id,
                customerId,
                productId,
                number,
                type,
                status,
                creditLimit,
                balance,
                available,
                interestRate,
                billingCycleDay,
                nextBillingDate,
                nextPaymentDate,
                installments,
                dueDate,
                overdue,
                createdAt,
                updatedAt
        );
    }


    public Credit withDueDate(Instant duedate) {
        return new Credit(
                id,
                customerId,
                productId,
                number,
                type,
                status,
                creditLimit,
                balance,
                available,
                interestRate,
                billingCycleDay,
                nextBillingDate,
                nextPaymentDate,
                installments,
                duedate,
                overdue,
                createdAt,
                updatedAt
        );
    }

    public Credit withCreatedAt(Instant createdAt) {
        return new Credit(
                id,
                customerId,
                productId,
                number,
                type,
                status,
                creditLimit,
                balance,
                available,
                interestRate,
                billingCycleDay,
                nextBillingDate,
                nextPaymentDate,
                installments,
                dueDate,
                overdue,
                createdAt,
                updatedAt
        );
    }

    public Credit withBillingInfo(Instant nextBillingDate, Instant nextPaymentDate, Instant dueDate) {
        return new Credit(
                id,
                customerId,
                productId,
                number,
                type,
                status,
                creditLimit,
                balance,
                available,
                interestRate,
                billingCycleDay,
                nextBillingDate,
                nextPaymentDate,
                installments,
                dueDate,
                overdue,
                createdAt,
                updatedAt
        );
    }

    public Credit applyPayment(BigDecimal amount) {

        BigDecimal newBalance = this.balance.subtract(amount);

        BigDecimal newAvailable = this.creditLimit.subtract(newBalance);

        CreditStatus newStatus =
                newBalance.compareTo(BigDecimal.ZERO) <= 0
                        ? CreditStatus.PAID
                        : CreditStatus.ACTIVE;

        return new Credit(
                id,
                customerId,
                productId,
                number,
                type,
                newStatus,
                creditLimit,
                newBalance,
                newAvailable,
                interestRate,
                billingCycleDay,
                nextBillingDate,
                nextPaymentDate,
                installments,
                dueDate,
                overdue,
                createdAt,
                Instant.now()
        );
    }

    public Credit debit(BigDecimal amount) {

        System.out.println("this.available "+this.available+" amount "+amount);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Invalid debit amount");
        }

        if (this.available.compareTo(amount) < 0) {
            throw new BusinessRuleException("Insufficient available credit");
        }

        BigDecimal newBalance = this.balance.add(amount);
        BigDecimal newAvailable = this.creditLimit.subtract(newBalance);

        return new Credit(
                id,
                customerId,
                productId,
                number,
                type,
                CreditStatus.ACTIVE,   // sigue activo
                creditLimit,
                newBalance,
                newAvailable,
                interestRate,
                billingCycleDay,
                nextBillingDate,
                nextPaymentDate,
                installments,
                dueDate,
                overdue,
                createdAt,
                Instant.now()
        );
    }

    public Credit credit(BigDecimal amount) {

        System.out.println("this.available "+this.available+" amount "+amount);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Invalid credit amount");
        }

        BigDecimal newBalance = this.balance.subtract(amount);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleException("Payment exceeds outstanding balance");
        }

        BigDecimal newAvailable = this.creditLimit.subtract(newBalance);

        CreditStatus newStatus =
                newBalance.compareTo(BigDecimal.ZERO) == 0
                        ? CreditStatus.PAID
                        : CreditStatus.ACTIVE;

        return new Credit(
                id,
                customerId,
                productId,
                number,
                type,
                newStatus,
                creditLimit,
                newBalance,
                newAvailable,
                interestRate,
                billingCycleDay,
                nextBillingDate,
                nextPaymentDate,
                installments,
                dueDate,
                overdue,
                createdAt,
                Instant.now()
        );
    }

}