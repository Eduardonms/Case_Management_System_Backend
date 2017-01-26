package se.teknikhogskolan.springcasemanagement.security.exception;

public class EncodingException extends RuntimeException {
    public EncodingException(String message, Exception e) {
        super(message, e);
    }

    public EncodingException(String message) {
        super(message);
    }
}
