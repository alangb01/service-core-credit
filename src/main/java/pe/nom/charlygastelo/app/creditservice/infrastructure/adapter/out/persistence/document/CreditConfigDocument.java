package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "credit_config")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditConfigDocument {

    @Id
    private String creditType;   // REVOLVING, PERSONAL, VEHICULAR

    private Double interestRateDaily;
    private Double interestRateMonthly;

    private Boolean allowWithdrawal;
    private Double maxWithdrawalPercentage;
}
