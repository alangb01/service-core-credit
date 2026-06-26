package pe.nom.charlygastelo.app.creditservice.domain.exception;

public class InsufficientCreditException extends RuntimeException {

    public InsufficientCreditException(String message) {
        super(message);
    }

}