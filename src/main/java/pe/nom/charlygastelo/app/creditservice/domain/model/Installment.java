package pe.nom.charlygastelo.app.creditservice.domain.model;

import java.math.BigDecimal;

public record Installment(
        int number,
        BigDecimal totalAmount,
        BigDecimal remainingAmount,
        InstallmentStatus status
) {

    public Installment(int number, BigDecimal totalAmount) {
        this(number, totalAmount, totalAmount, InstallmentStatus.PENDING);
    }


    public Installment applyPayment(BigDecimal amount) {
        BigDecimal newRemaining = remainingAmount.subtract(amount);

        InstallmentStatus newStatus =
                newRemaining.compareTo(BigDecimal.ZERO) <= 0
                        ? InstallmentStatus.PAID
                        : InstallmentStatus.PENDING;

        return new Installment(
                number,
                totalAmount,
                newRemaining,
                newStatus
        );
    }

    public boolean isPaid() {
        return status == InstallmentStatus.PAID;
    }
}
