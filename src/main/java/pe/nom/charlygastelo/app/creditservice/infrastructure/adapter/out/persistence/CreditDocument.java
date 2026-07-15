package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.persistence;

import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.nom.charlygastelo.app.creditservice.domain.model.CreditStatus;
import pe.nom.charlygastelo.app.creditservice.domain.model.CreditType;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "credits")
public class CreditDocument {

    @Id
    private String id;

    private String customerId;

    private String productId;

    private String number;

    private CreditType type;

    private CreditStatus status;

    private BigDecimal creditLimit;

    private BigDecimal balance;

    private BigDecimal available;

    private BigDecimal interestRate;

    private Integer billingCycleDay;
    private Instant nextBillingDate;     // fecha de corte
    private Instant nextPaymentDate;     // fecha de pago

    private Integer installments;

    private Instant dueDate;

    private boolean overdue;

    private Instant createdAt;

    private Instant updatedAt;


}