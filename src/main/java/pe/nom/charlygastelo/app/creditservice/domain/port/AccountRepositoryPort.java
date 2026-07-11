package pe.nom.charlygastelo.app.creditservice.domain.port;

import java.math.BigDecimal;
import io.reactivex.rxjava3.core.Single;
import pe.nom.charlygastelo.app.creditservice.domain.model.Account;

public interface AccountRepositoryPort {

    Single<Account> getById(String accountId);

    Single<Boolean> debitAccount(
            String transactionId,
            String customerId,
            String accountId,
            BigDecimal amount,
            String description
    );
}