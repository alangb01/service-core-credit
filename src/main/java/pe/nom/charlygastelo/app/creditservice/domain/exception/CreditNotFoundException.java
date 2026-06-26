package pe.nom.charlygastelo.app.creditservice.domain.exception;

public class CreditNotFoundException extends RuntimeException {

    public CreditNotFoundException(String message) {
        super(message);
    }

}