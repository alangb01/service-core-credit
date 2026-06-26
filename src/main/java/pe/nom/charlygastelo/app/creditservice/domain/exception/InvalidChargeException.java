package pe.nom.charlygastelo.app.creditservice.domain.exception;

public class InvalidChargeException extends RuntimeException {

    public InvalidChargeException(String message) {
        super(message);
    }

}