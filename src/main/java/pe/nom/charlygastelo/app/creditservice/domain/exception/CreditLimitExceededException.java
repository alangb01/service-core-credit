package pe.nom.charlygastelo.app.creditservice.domain.exception;

public class CreditLimitExceededException extends RuntimeException {

    public CreditLimitExceededException(String message) {
        super(message);
    }

}