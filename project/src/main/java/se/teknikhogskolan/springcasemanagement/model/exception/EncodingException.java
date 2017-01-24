package se.teknikhogskolan.springcasemanagement.model.exception;

public class EncodingException extends Exception {
    public EncodingException(String message, Exception e) {
        super(message, e);
    }

    public EncodingException(String message) {
        super(message);
    }
}
