package pe.nom.charlygastelo.app.creditservice.domain.exception;

public class CreditInactiveException extends RuntimeException {

    public CreditInactiveException(String message) {
        super(message);
    }

}