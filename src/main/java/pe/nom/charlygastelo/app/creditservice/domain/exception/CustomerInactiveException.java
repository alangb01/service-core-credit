package pe.nom.charlygastelo.app.creditservice.domain.exception;

public class CustomerInactiveException extends RuntimeException {

    public CustomerInactiveException(String message) {
        super(message);
    }

}