package pe.nom.charlygastelo.app.creditservice.domain.port;

import io.reactivex.rxjava3.core.Single;
import pe.nom.charlygastelo.app.creditservice.domain.model.Account;

import java.math.BigDecimal;

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