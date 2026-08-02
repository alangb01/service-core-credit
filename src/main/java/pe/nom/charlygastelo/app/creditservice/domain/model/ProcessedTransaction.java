package pe.nom.charlygastelo.app.creditservice.domain.model;

public record ProcessedTransaction(
    Transaction transaction,
    Credit credit,
    Account account
) {

}
