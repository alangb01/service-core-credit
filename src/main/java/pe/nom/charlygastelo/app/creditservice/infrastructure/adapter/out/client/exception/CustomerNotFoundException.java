package pe.nom.charlygastelo.app.creditservice.infrastructure.adapter.out.client.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
