package pe.nom.charlygastelo.app.creditservice.domain.exception;

public class CreditCacheException extends RuntimeException {
    public CreditCacheException(String message) {
        super(message);
    }

    public CreditCacheException(String message, Throwable cause) {
        super(message, cause);
    }
}
