package pe.nom.charlygastelo.app.creditservice.domain.exception;

public class CreditAlreadyExistsException extends RuntimeException {

    public CreditAlreadyExistsException(String message) {
        super(message);
    }

}