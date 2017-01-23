package se.teknikhogskolan.springcasemanagement.model.exception;

public class ModelException extends Exception {
    public ModelException(String message, Exception e) {
        super(message, e);
    }

    public ModelException(String message) {
        super(message);
    }
}
