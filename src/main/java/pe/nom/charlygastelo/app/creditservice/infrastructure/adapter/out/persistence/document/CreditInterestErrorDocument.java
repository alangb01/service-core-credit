package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.persistence.document;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Document(collection = "credit_interest_errors")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditInterestErrorDocument {

    @Id
    private String id;

    private String creditId;
    private String cycle;

    private String errorMessage;
    private String stackTrace;

    private Instant occurredAt;
}
