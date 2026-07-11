package pe.nom.charlygastelo.app.creditservice.domain.port;

import java.math.BigDecimal;
import io.reactivex.rxjava3.core.Single;

public interface AccountDebitEventPort {

    Single<Boolean> debitAccount(
            String transactionId,
            String customerId,
            String accountId,
            BigDecimal amount,
            String description
    );
}