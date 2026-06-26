package pe.nom.charlygastelo.app.creditservice.domain.exception;

public class InvalidPaymentException extends RuntimeException {

    public InvalidPaymentException(String message) {
        super(message);
    }

}