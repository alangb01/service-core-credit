package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.persistence.document;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "credit_billing_cycles")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditBillingCycleDocument {

    @Id
    private String id;

    private String creditId;

    private Instant cycleStart;
    private Instant cycleEnd;

    private String interestAccrued;   // BigDecimal como string
    private String status;            // OPEN, CLOSED
}
